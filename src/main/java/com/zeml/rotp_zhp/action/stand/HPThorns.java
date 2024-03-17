package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.capability.entity.hamonutil.EntityHamonChargeCapProvider;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.Optional;

public class HPThorns extends StandEntityAction {

    private EffectInstance hamonthorns = null;
    public HPThorns(Builder builder){
        super(builder);
    }



    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target){
        if (power.getStamina()>0){
            return ActionConditionResult.POSITIVE;
        }
        return ActionConditionResult.NEGATIVE;
    }


    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide) {
            LivingEntity user = userPower.getUser();
            INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower -> {
                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                if(hamonOp.isPresent()){
                    HamonData hamon = hamonOp.get();
                    if (hamon.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get()) && ipower.getEnergy()>0){
                        user.playSound(ModSounds.HAMON_CONCENTRATION.get(),1,1);
                        float hamonEfficiency = hamon.getActionEfficiency(20, true);
                        user.getCapability(EntityHamonChargeCapProvider.CAPABILITY).ifPresent(cap ->
                                cap.setHamonCharge(0.2F * hamon.getHamonDamageMultiplier() * hamonEfficiency, 20, user, 20)
                        );
                        hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.CONTROL,1);
                    }
                }
            });
        }
    }


}
