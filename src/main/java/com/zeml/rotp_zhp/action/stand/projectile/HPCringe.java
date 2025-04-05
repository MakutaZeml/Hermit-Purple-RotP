package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HPCringe extends StandEntityAction {
    public HPCringe(StandEntityAction.Builder builder){
        super(builder);
    }


    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if(user instanceof PlayerEntity){
            return super.checkSpecificConditions(user, power, target);

        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    public boolean enabledInHudDefault() {
        return false;
    }


}