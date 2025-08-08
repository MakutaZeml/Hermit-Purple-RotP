package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.action.stand.projectile.HPGrabCommand;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Optional;

public class HPGrabOverdrive extends StandEntityAction {
    public HPGrabOverdrive(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        Optional<Boolean> overdrive = INonStandPower.getNonStandPowerOptional(user).map(ipower ->{
            boolean returning = false;
            Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
            if(hamonOp.isPresent()){
                HamonData hamon = hamonOp.get();
                returning = hamon.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get());
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
                            hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH,5);
                            StandHamonDamage.dealHamonDamage(target, 5,userPower.getUser() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()), userPower,1.5F,1);
                            ipower.consumeEnergy(.2F*ipower.getMaxEnergy());
                        }
                    });
                }
            }
        }
    }

    @Override
    protected SoundEvent getShout(LivingEntity user, IStandPower power, ActionTarget target, boolean wasActive){
        if(power.getStandInstance()
                .flatMap(StandSkinsManager.getInstance()::getStandSkin).map(standSkin -> standSkin.resLoc).isPresent()){
            if(power.getStandInstance()
                    .flatMap(StandSkinsManager.getInstance()::getStandSkin).map(standSkin -> standSkin.resLoc).get().toString().contains("jonathan")){
                return InitSounds.JONATHAN_OVER_DRIVE.get();
            }
        }
        return super.getShout(user,power,target,wasActive);
    }



}
