package com.zeml.rotp_zhp.mixin;

import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.ZoomPunchEntity;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.MathUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zhp.HermitConfig;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.BiPredicate;

@Mixin(value = ZoomPunchEntity.class, remap = false)
public class MixinZoomPunchEntity extends OwnerBoundProjectileEntity {

    public MixinZoomPunchEntity(EntityType<? extends OwnerBoundProjectileEntity> entityType, @NotNull LivingEntity owner, World world) {
        super(entityType, owner, world);
    }

    @Inject(method = "standDamage", at = @At("RETURN"), cancellable = true)
    private void standDamage(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(this.getOwner() != null && IStandPower.getStandPowerOptional(this.getOwner()).map(isStandPower -> isStandPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false));
    }

    @Inject(method = "hurtTarget", at =@At("HEAD"))
    private void hurtTarget(Entity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir){
        if(IStandPower.getStandPowerOptional(owner).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false) && HermitConfig.getCommonConfigInstance(false).hermitHamon.get()){
            IStandPower.getStandPowerOptional(owner).ifPresent(standPower -> {

                boolean spendHamonStability = ((ZoomPunchEntityAccesor) ((ZoomPunchEntity)(Object)this)).getSpendHamonStability();
                float hamonDamage = ((ZoomPunchEntityAccesor) ((ZoomPunchEntity)(Object)this)).getHamonDamage();
                float hamonDamageCost = ((ZoomPunchEntityAccesor) ((ZoomPunchEntity)(Object)this)).getHamonDamageCost();

                if(!isRetracting()) {
                    boolean regularAttack = super.hurtTarget(target, owner);
                    boolean hamonAttack = hermit_Purple_RotP$userHamon((power, hamon) -> {


                        boolean hasEnergy = power.getEnergy() > 0;
                        if (!(hasEnergy || spendHamonStability)) return false;
                        Boolean dealtDamage = hamon.consumeHamonEnergyTo(eff -> {

                            boolean gaveHamonPointsForBaseHit = ((ZoomPunchEntityAccesor) ((ZoomPunchEntity)(Object)this)).getGaveHamonPointsForBaseHit();
                            float baseHitPoints =  ((ZoomPunchEntityAccesor) ((ZoomPunchEntity)(Object)this)).getBaseHitPoints();

                            boolean dealtHamonDamage = DamageUtil.dealHamonDamage(target, hamonDamage * eff, this, owner);

                            if (hasEnergy && dealtHamonDamage) {
                                hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH, Math.min(hamonDamageCost, power.getEnergy()) * eff);
                            }

                            if (!gaveHamonPointsForBaseHit) {
                                gaveHamonPointsForBaseHit = true;
                                hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH, baseHitPoints);
                            }

                            return dealtHamonDamage;
                        }, hamonDamageCost, ModHamonSkills.ZOOM_PUNCH.get());
                        return dealtDamage != null && dealtDamage;
                    });

                    if (regularAttack) {
                        float knockback = (float) owner.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                        if (knockback > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity) target).knockback(knockback * 0.5F,
                                        (double) MathHelper.sin(owner.yRot * MathUtil.DEG_TO_RAD),
                                        (double)(-MathHelper.cos(owner.yRot * MathUtil.DEG_TO_RAD)));
                            } else {
                                target.push(
                                        (double)(-MathHelper.sin(owner.yRot * MathUtil.DEG_TO_RAD) * knockback * 0.5F),
                                        0.1D,
                                        (double)(MathHelper.cos(owner.yRot * MathUtil.DEG_TO_RAD) * knockback * 0.5F));
                            }

                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }
                    }
                    cir.setReturnValue(regularAttack || hamonAttack); ;
                }
                cir.setReturnValue(false);
            });
            return;
        }

    }


    @Override
    protected float movementSpeed() {
        return ((ZoomPunchEntityAccesor)((ZoomPunchEntity)(Object) this)).getSpeed();
    }

    @Override
    protected float getBaseDamage() {
        LivingEntity owner = getOwner();
        return owner != null ? DamageUtil.getDamageWithoutHeldItem(owner)
                : (float) Attributes.ATTACK_DAMAGE.getDefaultValue();
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0;
    }

    @Override
    public boolean standDamage() {
        return this.getOwner() != null && HermitConfig.getCommonConfigInstance(false).hermitHamon.get() && IStandPower.getStandPowerOptional(this.getOwner()).map(isStandPower -> isStandPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false);
    }


    private Optional<INonStandPower> userPower = ((ZoomPunchEntityAccesor) ((ZoomPunchEntity)(Object)this)).getUserPower();
    private Optional<HamonData> hamon = ((ZoomPunchEntityAccesor)((ZoomPunchEntity)(Object)this)).getHamon();

    @Unique
    private boolean hermit_Purple_RotP$userHamon(BiPredicate<INonStandPower, HamonData> action) {
        if (!userPower.isPresent()) {
            userPower = INonStandPower.getNonStandPowerOptional(getOwner()).resolve();
        }
        if (userPower.isPresent()) {
            if (!hamon.isPresent()) {
                hamon = userPower.flatMap(power -> power.getTypeSpecificData(ModPowers.HAMON.get()));
            }
            if (hamon.isPresent()) {
                return action.test(userPower.get(), hamon.get());
            }
        }
        return false;
    }
}
