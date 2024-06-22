package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonActions;
import com.github.standobyte.jojo.network.packets.fromserver.HamonSyncOnLoadPacket;
import com.github.standobyte.jojo.network.packets.fromserver.TrHamonEnergyTicksPacket;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HamonBreath extends StandEntityAction {
    public HamonBreath(StandEntityAction.Builder builder){
        super(builder);
    }


    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target){
        AtomicBoolean ham = new AtomicBoolean(false);
        INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower -> {
            if (ipower.getType() == ModPowers.HAMON.get()) {
                ham.set(true);
            }
        });

        if (ham.get()){
            if (user.getAirSupply() < user.getMaxAirSupply()) {
                return conditionMessage("no_air");
            }
            return ActionConditionResult.POSITIVE;
        }
        return conditionMessage("no_hamon");
    }



    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide){
            LivingEntity user = userPower.getUser();
            INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower ->{
                AtomicReference<Float> tics = new AtomicReference<>((float) 0);
                 ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(
                        hamonData -> {
                             tics.set(hamonData.getBreathingLevel()*hamonData.getMaxBreathStability() / hamonData.getMaxEnergy());

                            return null;

                        }
                );
                 ipower.addEnergy(10+tics.get());
                 ipower.setHeldAction(ModHamonActions.HAMON_BREATH.get());
            });
        }

    }

/*
    @Override
    public void stoppedHolding(World world, LivingEntity user, IStandPower power, int ticksHeld, boolean willFire) {
        if(getHermitPurple(user).isPresent()){
            INonStandPower.getNonStandPowerOptional(user).ifPresent(ipower->{
                getHermitPurple(user).get().hamon(ticksHeld);
                ipower.getTypeSpecificData(ModPowers.HAMON.get()).map(
                        hamonData -> {
                            return null;
                        }
                );

            });
        }
        invokeForStand(power, StandEntity::stopTaskWithRecovery);
    }

 */

    @Override
    public boolean enabledInHudDefault() {
        return false;
    }


}
