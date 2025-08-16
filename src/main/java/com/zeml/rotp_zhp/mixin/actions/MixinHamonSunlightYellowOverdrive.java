package com.zeml.rotp_zhp.mixin.actions;

import com.github.standobyte.jojo.action.non_stand.HamonSunlightYellowOverdrive;
import com.github.standobyte.jojo.action.player.ContinuousActionInstance;
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
import com.github.standobyte.jojo.util.mc.damage.KnockbackCollisionImpact;
import com.zeml.rotp_zhp.HermitConfig;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HamonSunlightYellowOverdrive.class, remap = false)
public class MixinHamonSunlightYellowOverdrive{



    @Mixin(value = HamonSunlightYellowOverdrive.Instance.class, remap = false)
    public static class Instance extends ContinuousActionInstance<HamonSunlightYellowOverdrive, INonStandPower>{
        @Shadow protected float energySpentRatio;
        @Shadow protected HamonData userHamon;
        protected float energyRatio = this.energySpentRatio;
        protected HamonData hamonData = this.userHamon;
        public Instance(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, HamonSunlightYellowOverdrive action) {
            super(user, userCap, playerPower, action);
        }

        @Inject(method = "doHamonAttack", at = @At("HEAD") ,cancellable = true)
        private void doHamonAttack(LivingEntity target, CallbackInfo ci){
            if(IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false) &&
                    HermitConfig.getCommonConfigInstance(false).hermitHamon.get()) {
                IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {
                    RotpHermitPurpleAddon.LOGGER.debug("PASO? {}, {}",IStandPower.getStandPowerOptional(user).map(stand -> stand.getStandManifestation() instanceof HermitPurpleEntity).orElse(false), standPower );

                    float efficiency = hamonData.getActionEfficiency(0, true, getAction().getUnlockingSkill());
                    float damage = 3.25F + 6.75F * energyRatio;
                    damage *= efficiency;

                    if (StandHamonDamage.dealHamonDamage(target, damage, user, null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_YELLOW.get()),standPower,1,1)) {
                        target.level.playSound(null, target.getX(), target.getEyeY(), target.getZ(), ModSounds.HAMON_SYO_PUNCH.get(), target.getSoundSource(), energyRatio, 1.0F);
                        hamonData.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH, playerPower.getTypeSpecificData(ModPowers.HAMON.get()).map(HamonData::getMaxBreathStability).orElse(playerPower.getMaxEnergy()) * energyRatio * efficiency);
                        target.knockback(2.5F, user.getX() - target.getX(), user.getZ() - target.getZ());
                        boolean hamonSpread = hamonData.isSkillLearned(ModHamonSkills.HAMON_SPREAD.get());
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
                                cap.hamonDamage(punchDamage, 0, ModParticles.HAMON_SPARK_YELLOW.get());
                            }
                        });
                    }

                });

                ci.cancel();
            }
        }

    }


}
