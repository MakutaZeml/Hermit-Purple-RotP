package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.playeranim.anim.ModPlayerAnimations;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.zeml.rotp_zhp.client.playeranim.anim.AddonPlayerAnimations;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineEntity;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineGrabEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        boolean hm2 = INonStandPower.getNonStandPowerOptional(power.getUser()).map(ipower->{
            boolean value = false;
            if (ipower.getType() == ModPowers.HAMON.get()){

                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                HamonData hamon = hamonOp.get();

                value = hamon.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get());
            }
            return value;
        }).orElse(false);


        HermitPurpleEntity HP =  (HermitPurpleEntity) power.getStandManifestation();
        if(HP != null){
            if(getLandedVineStand(HP).isPresent() && hm2){
                return InitStands.HP_GRAB_OVERDRIVE.get();
            }
        }
        return this;
    }



    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            HPVineGrabEntity vineGrab = new HPVineGrabEntity(world, standEntity);
            vineGrab.withStandSkin(standEntity.getStandSkin());
            standEntity.addProjectile(vineGrab);

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
    public boolean noFinisherBarDecay() {
        return true;
    }


    @Override
    public boolean clHeldStartAnim(PlayerEntity user) {
        return AddonPlayerAnimations.grab.setWindupAnim(user);
    }

    @Override
    public void clHeldStopAnim(PlayerEntity user) {
        AddonPlayerAnimations.grab.stopAnim(user);
    }

    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        super.onTaskStopped(world, standEntity, standPower, task, newAction);
    }
}
