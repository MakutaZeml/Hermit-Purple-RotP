package com.zeml.rotp_zhp.util;


import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.advancements.ModCriteriaTriggers;
import com.github.standobyte.jojo.capability.entity.LivingUtilCapProvider;
import com.github.standobyte.jojo.capability.entity.hamonutil.EntityHamonChargeCap;
import com.github.standobyte.jojo.capability.entity.hamonutil.EntityHamonChargeCapProvider;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonUtil;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

import static com.github.standobyte.jojo.util.mc.damage.DamageUtil.hurtThroughInvulTicks;


public class StandHamonDamage {

    public static boolean dealHamonDamage(Entity target, float amount, @Nullable Entity srcDirect, @Nullable Entity srcIndirect, @Nullable Consumer<StandHamonAttackProperties> attackProperties, IStandPower standPower, float standVampirism, float standNormal) {
        if (target instanceof LivingEntity) {
            LivingEntity livingTarget;

            if(target instanceof StandEntity){
                livingTarget = ((StandEntity) target).getUser();
            } else {
                livingTarget = (LivingEntity) target;
            }

            StandHamonAttackProperties attack = new StandHamonAttackProperties();
            if (attackProperties != null) {
                attackProperties.accept(attack);
            }

            if (livingTarget.getCapability(EntityHamonChargeCapProvider.CAPABILITY).map(EntityHamonChargeCap::hasHamonCharge).orElse(false)) {
                return false;
            }

            Optional<INonStandPower> targetPower = INonStandPower.getNonStandPowerOptional(livingTarget).resolve();

            if (INonStandPower.getNonStandPowerOptional(livingTarget).resolve().flatMap(
                    power -> power.getTypeSpecificData(ModPowers.PILLAR_MAN.get())
                            .map(pillarman -> pillarman.isStoneFormEnabled())).orElse(false)) {
                return false;
            }

            boolean scarf = livingTarget.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ModItems.SATIPOROJA_SCARF.get();
            if (scarf) {
                if (targetPower.map(power -> power.getType() == ModPowers.HAMON.get()).orElse(false)) {
                    return false;
                }
                amount *= 0.25F;
            }

            DamageSource dmgSource = srcDirect == null ? DamageUtil.HAMON :
                    srcIndirect == null ? new EntityDamageSource(DamageUtil.HAMON.getMsgId() + ".entity", srcDirect).bypassArmor() :
                            new IndirectEntityDamageSource(DamageUtil.HAMON.getMsgId() + ".entity", srcDirect, srcIndirect).bypassArmor();

            boolean undeadTarget = JojoModUtil.isAffectedByHamon(livingTarget);
            if (!undeadTarget) {
                amount *= 0.2F;
                if(target instanceof StandEntity){
                    amount *= standNormal;
                }
            }
            else if (INonStandPower.getNonStandPowerOptional(livingTarget)
                    .map(power -> power.getType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
                amount *= 0.5F;
            }else if(target instanceof StandEntity){
                amount *= standVampirism;
            }

            final float dmgAmount = amount;
            if (attack.srcEntityHamonMultiplier && dmgSource.getEntity() instanceof LivingEntity) {
                LivingEntity sourceLiving = (LivingEntity) dmgSource.getEntity();
                float hamonMultiplier = INonStandPower.getNonStandPowerOptional(sourceLiving).map(power ->
                        power.getTypeSpecificData(ModPowers.HAMON.get()).map(hamon -> {
                            float hamonStrengthMultiplier = hamon.getHamonDamageMultiplier();
                            if (undeadTarget && !scarf && hamon.isSkillLearned(ModHamonSkills.HAMON_SPREAD.get())) {
                                livingTarget.getCapability(LivingUtilCapProvider.CAPABILITY)
                                        .ifPresent(cap -> cap.hamonSpread(dmgAmount * hamonStrengthMultiplier));
                            }
                            return hamonStrengthMultiplier;
                        }).orElse(1F)).orElse(1F);
                amount *= hamonMultiplier;
            }


            amount *= JojoModConfig.getCommonConfigInstance(false).hamonDamageMultiplier.get().floatValue();

//            JojoMod.LOGGER.debug(amount);

            if (hurtThroughInvulTicks(target, new StandEntityDamageSource(DamageUtil.HAMON.getMsgId() + ".entity", srcDirect, standPower), amount)) {
                HamonUtil.createHamonSparkParticlesEmitter(target, amount / (HamonData.MAX_HAMON_STRENGTH_MULTIPLIER * 5), attack.soundVolumeMultiplier, attack.hamonParticle);
                if (scarf && undeadTarget && livingTarget instanceof ServerPlayerEntity) {
                    ModCriteriaTriggers.VAMPIRE_HAMON_DAMAGE_SCARF.get().trigger((ServerPlayerEntity) livingTarget);
                }
                return true;
            }
        }
        return false;
    }


    public static class StandHamonAttackProperties{
        private IParticleData hamonParticle = ModParticles.HAMON_SPARK.get();
        private boolean srcEntityHamonMultiplier = true;
        private float soundVolumeMultiplier = 1.0F;

        public StandHamonAttackProperties hamonParticle(IParticleData particleType) {
            this.hamonParticle = particleType != null ? particleType : ModParticles.HAMON_SPARK.get();
            return this;
        }

        public StandHamonAttackProperties noSrcEntityHamonMultiplier() {
            this.srcEntityHamonMultiplier = false;
            return this;
        }

        public StandHamonAttackProperties soundVolumeMultiplier(float multiplier) {
            this.soundVolumeMultiplier = multiplier;
            return this;
        }
    }

}
