package com.zeml.rotp_zhp.mixin.actions;

import com.github.standobyte.jojo.action.non_stand.HamonScarletOverdrive;
import com.github.standobyte.jojo.action.non_stand.HamonSunlightYellowOverdrive;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.damage.KnockbackCollisionImpact;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HamonScarletOverdrive.class, remap = false)
public class MixinHamonScarletOverdrive {


    @Mixin(value = HamonScarletOverdrive.Instance.class, remap = false)
    public static class MixinInstance extends HamonSunlightYellowOverdrive.Instance{
        public MixinInstance(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, HamonSunlightYellowOverdrive action, float spentEnergy) {
            super(user, userCap, playerPower, action, spentEnergy);
        }

        @Inject(method = "doHamonAttack", at = @At("HEAD") ,cancellable = true)
        private void doHamonAttack(LivingEntity target, CallbackInfo ci){

            if(IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false)) {
                IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {
                    float efficiency = userHamon.getActionEfficiency(0, true, getAction().getUnlockingSkill());
                    float damage = 2.5F + 5F * energySpentRatio;
                    damage *= efficiency;
                    int fireSeconds = MathHelper.floor(2 + 8F * (float) userHamon.getHamonStrengthLevel() / (float) HamonData.MAX_STAT_LEVEL * efficiency);
                    float hamonDamage = damage;
                    if (DamageUtil.dealDamageAndSetOnFire(target,
                            entity -> StandHamonDamage.dealHamonDamage(entity, hamonDamage, user, null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_RED.get()),standPower,1.5F,1),
                            fireSeconds, true)) {
                        target.level.playSound(null, target.getX(), target.getEyeY(), target.getZ(), ModSounds.HAMON_SYO_PUNCH.get(), target.getSoundSource(), energySpentRatio, 1.0F);
                        userHamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH, playerPower.getTypeSpecificData(ModPowers.HAMON.get()).map(HamonData::getMaxBreathStability).orElse(playerPower.getMaxEnergy()) * energySpentRatio * efficiency);
                        DamageUtil.knockback3d(target, 2f, -5, user.yRot);
                        boolean hamonSpread = userHamon.isSkillLearned(ModHamonSkills.HAMON_SPREAD.get());
                        float punchDamage = damage;

                        LivingEntity userTarget;
                        if(target instanceof StandEntity && ((StandEntity) target).getUser() != null){
                            userTarget =  ((StandEntity) target).getUser();
                        } else {
                            userTarget = target;
                        }

                        KnockbackCollisionImpact.getHandler(userTarget).ifPresent(cap -> {
                            cap.onPunchSetKnockbackImpact(userTarget.getDeltaMovement(), user);
                            if (hamonSpread) {
                                cap.hamonDamage(punchDamage, Math.max(20 * fireSeconds / 2, 20), ModParticles.HAMON_SPARK_RED.get());
                            }
                        });
                    }
                });
                ci.cancel();
            }
        }
    }

}
