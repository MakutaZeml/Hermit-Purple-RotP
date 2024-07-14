package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineGrabEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class HPGrabCommand extends StandEntityAction {
    public HPGrabCommand(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target){
        AtomicBoolean hm= new AtomicBoolean(false);
        INonStandPower.getNonStandPowerOptional(power.getUser()).ifPresent(ipower->{
            if (ipower.getType() == ModPowers.HAMON.get()){
                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                HamonData hamon = hamonOp.get();
                if(hamon.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get())){
                    hm.set(true);
                }
            }
        });
        HermitPurpleEntity HP = MCUtil.entitiesAround(HermitPurpleEntity.class,power.getUser(),3,false, stand ->stand.getUser() == power.getUser()).stream().findAny().orElse(null);
        if(HP != null){
            if(getLandedVineStand(HP).isPresent() && hm.get()){
                return InitStands.HP_GRAB_OVERDRIVE.get();
            }
        }
        return super.replaceAction(power,target);
    }

    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            standEntity.addProjectile(new HPVineGrabEntity(world, standEntity));

        }
    }

    @Override
    public void stoppedHolding(World world, LivingEntity user, IStandPower power, int ticksHeld, boolean willFire) {
        invokeForStand(power, stand -> {
            if (stand.getCurrentTaskAction() == this) {
                if (getLandedVineStand(stand).isPresent()) {
                    return;
                } else {
                    stand.stopTaskWithRecovery();
                }
            }
        });
    }

    public static Optional<HPVineGrabEntity> getLandedVineStand(StandEntity stand) {
        List<HPVineGrabEntity> vineLanded = stand.level.getEntitiesOfClass(HPVineGrabEntity.class,
                stand.getBoundingBox().inflate(16), redBind -> stand.is(redBind.getOwner()) && redBind.isAttachedToAnEntity());
        return !vineLanded.isEmpty() ? Optional.of(vineLanded.get(0)) : Optional.empty();
    }

    /*
    public static Optional<HPVineGrabEntity> getLandedVineUser(LivingEntity user) {
        List<HPVineGrabEntity> vineLanded = user.level.getEntitiesOfClass(HPVineGrabEntity.class,
                user.getBoundingBox().inflate(16), redBind -> user.is(Objects.requireNonNull(((StandEntity) redBind.getOwner()).getUser())) && redBind.isAttachedToAnEntity());
        return !vineLanded.isEmpty() ? Optional.of(vineLanded.get(0)) : Optional.empty();
    }
     */


    @Override
    public boolean noFinisherDecay() {
        return true;
    }


    @Override
    public StandAction[] getExtraUnlockable() {
        return new StandAction[] { InitStands.HP_GRAB_OVERDRIVE.get()};
    }

}
