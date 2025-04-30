package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zhp.action.stand.projectile.HPGrabCommand;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Optional;

public class HPGrabScarlet extends StandEntityAction {
    public HPGrabScarlet(StandEntityAction.Builder builder){
        super(builder);
    }


    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        Optional<Boolean> overdrive = INonStandPower.getNonStandPowerOptional(user).map(ipower ->{
            boolean returning = false;
            Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
            if(hamonOp.isPresent()){
                HamonData hamon = hamonOp.get();
                returning = hamon.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get());
            }
            return returning;
        });
        boolean condition = overdrive.orElse(false);
        return condition?ActionConditionResult.POSITIVE:ActionConditionResult.NEGATIVE;
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            if(HPGrabCommand.getLandedVineStand(userPower.getUser()).isPresent()){
                LivingEntity target = HPGrabCommand.getLandedVineStand(userPower.getUser()).get().getEntityAttachedTo();
                if(target != null){
                    INonStandPower.getNonStandPowerOptional(userPower.getUser()).ifPresent(ipower->{
                        Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                        if(hamonOp.isPresent()){
                            HamonData hamon = hamonOp.get();
                            float hamomlevel = hamon.getHamonStrengthLevel();
                            hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH,5);
                            DamageUtil.dealDamageAndSetOnFire(target,
                                    entity -> StandHamonDamage.dealHamonDamage(entity, 5, userPower.getUser() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_RED.get()), userPower,1.F,1F),
                                    MathHelper.floor(2 + 8F *  hamomlevel / (float) HamonData.MAX_STAT_LEVEL *2), false);
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean isUnlocked(IStandPower power) {
        return INonStandPower.getNonStandPowerOptional(power.getUser()).map(ipower ->ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get())).orElse(false)).orElse(false);
    }


    @Override
    public boolean isLegalInHud(IStandPower power) {
        return INonStandPower.getNonStandPowerOptional(power.getUser()).map(ipower ->ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get())).orElse(false)).orElse(false);
    }
}
