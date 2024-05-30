package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.util.ClientUtil;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HPTargetSelection extends StandEntityAction {

    public HPTargetSelection(StandEntityAction.Builder builder){
        super(builder);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(world.isClientSide){
            ClientUtil.openTargetSelection();
        }
    }
}
