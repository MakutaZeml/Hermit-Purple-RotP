package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.util.ClientUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HPTargetSelection extends StandEntityAction {

    public HPTargetSelection(StandEntityAction.Builder builder){
        super(builder);
    }

    @Override
    public ActionConditionResult checkConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        return user instanceof PlayerEntity?ActionConditionResult.POSITIVE:ActionConditionResult.NEGATIVE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(world.isClientSide && userPower.getUser() == com.github.standobyte.jojo.client.ClientUtil.getClientPlayer()){
            ClientUtil.openTargetSelection();
        }
    }
}
