package com.weever.rotp_mih.action.stand;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.itemprojectile.KnifeEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.world.World;

public class ThrowKnife extends StandEntityAction {
    public ThrowKnife(Builder builder) {
        super(builder);
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) { // ahhh unclestalin🤓
            KnifeEntity knife = new KnifeEntity (world, userPower.getUser());
            knife.pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
            knife.setTimeStopFlightTicks (5);
            knife.shootFromRotation(userPower.getUser (), 1.5F,1);
            world.addFreshEntity(knife);
        }
    }
}