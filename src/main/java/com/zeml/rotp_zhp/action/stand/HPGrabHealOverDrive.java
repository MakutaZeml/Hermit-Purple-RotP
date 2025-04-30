package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonUtil;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.action.stand.projectile.HPGrabCommand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class HPGrabHealOverDrive extends StandEntityAction {
    public HPGrabHealOverDrive(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        AtomicBoolean hm = new AtomicBoolean(false);
        INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower->{
            if (ipower.getType() == ModPowers.HAMON.get()){
                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                HamonData hamon = hamonOp.get();
                hm.set(hamon.isSkillLearned(ModHamonSkills.HEALING_TOUCH.get()));
            }
        });
        if(hm.get()){
            return ActionConditionResult.POSITIVE;
        }
        return conditionMessage("no_healing");
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            if(HPGrabCommand.getLandedVineStand(userPower.getUser()).isPresent()){
                LivingEntity target = HPGrabCommand.getLandedVineStand(userPower.getUser()).get().getEntityAttachedTo();
                if(target != null){
                    if(userPower.getUser() != null){
                        INonStandPower.getNonStandPowerOptional(userPower.getUser()).ifPresent(ipower ->{
                            if (ipower.getType() == ModPowers.HAMON.get()) {
                                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                                HamonData hamon = hamonOp.get();
                                float hamonControl = hamon.getHamonControlLevelRatio();
                                float hamonEfficiency = hamon.getActionEfficiency(750, true);
                                int regenDuration = (int) ((50F + hamonEfficiency * 50F) * (1 + hamonControl));
                                int regenLvl = MathHelper.clamp((int) ((hamonControl - 0.0001F) * 3 + (hamonEfficiency - 0.25F) * 4F/3F - 1), 0, 2);
                                updateRegenEffect(target,regenDuration,regenLvl);
                                if (hamon.isSkillLearned(ModHamonSkills.EXPEL_VENOM.get())) {
                                    target.removeEffect(Effects.POISON);
                                    target.removeEffect(Effects.WITHER);
                                    target.removeEffect(Effects.HUNGER);
                                    target.removeEffect(Effects.CONFUSION);
                                }
                                Vector3d sparksPos = new Vector3d(target.getX(), target.getY(0.5), target.getZ());
                                HamonUtil.emitHamonSparkParticles(world, null, sparksPos, Math.max(0.5F * hamonControl * hamonEfficiency, 0.1F));

                            }
                        });
                    }
                }
            }
        }
    }

    private void updateRegenEffect(LivingEntity entity, int duration, int level) {
        EffectInstance currentRegen = entity.getEffect(Effects.REGENERATION);
        if (currentRegen != null && currentRegen.getAmplifier() < 5 && currentRegen.getAmplifier() <= level) {
            int regenGap = 50 >> currentRegen.getAmplifier();
            if (regenGap > 0) {
                int oldRegenAppliesIn = currentRegen.getDuration() % (50 >> currentRegen.getAmplifier());
                int newRegenGap = 50 >> level;
                int newRegenAppliesIn = duration % newRegenGap;

                if (oldRegenAppliesIn > newRegenAppliesIn) {
                    int newDuration = duration + (oldRegenAppliesIn - newRegenAppliesIn);
                    while (newDuration > duration) {
                        newDuration -= newRegenGap;
                    }
                    if (newDuration > 0) {
                        duration = newDuration;
                    }
                }
                else {
                    duration -= (newRegenAppliesIn - oldRegenAppliesIn);
                }
            }
        }
        entity.addEffect(new EffectInstance(Effects.REGENERATION, duration, level));
    }

    @Override
    public boolean isUnlocked(IStandPower power) {
        return INonStandPower.getNonStandPowerOptional(power.getUser()).map(ipower ->ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.HEALING_TOUCH.get())).orElse(false)).orElse(false);
    }

    @Override
    public boolean isLegalInHud(IStandPower power) {
        return INonStandPower.getNonStandPowerOptional(power.getUser()).map(ipower ->ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.HEALING_TOUCH.get())).orElse(false)).orElse(false);
    }
}
