package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.client.playeranim.anim.AddonPlayerAnimations;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineGrabEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class HPGrabCommand extends StandEntityAction {
    public HPGrabCommand(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target) {
        boolean yellow = INonStandPower.getNonStandPowerOptional(power.getUser())
                .map(ipower->ipower.getTypeSpecificData(ModPowers.HAMON.get())
                        .map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get())
                                && hamonData.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get())).orElse(false)).orElse(false);
        boolean heal =INonStandPower.getNonStandPowerOptional(power.getUser())
                .map(ipower->ipower.getTypeSpecificData(ModPowers.HAMON.get())
                        .map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.HEALING_TOUCH.get())
                                && hamonData.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get())).orElse(false)).orElse(false);
        HermitPurpleEntity HP = (HermitPurpleEntity) power.getStandManifestation();
        if(HP != null && getLandedVineStand(power.getUser()).isPresent() ){
            if(yellow){
                if(heal){
                    return !power.getUser().isShiftKeyDown()? InitStands.HP_GRAB_OVERDRIVE.get(): InitStands.HP_HEAL_VINE.get();
                }
                return InitStands.HP_GRAB_OVERDRIVE.get();
            } else if (heal) {
                return InitStands.HP_HEAL_VINE.get();
            }
        }
        return replaceActionKostyl(power,target);
    }



    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            HPVineGrabEntity vineGrab = new HPVineGrabEntity(world, userPower.getUser(), standEntity);
            vineGrab.withStandSkin(standEntity.getStandSkin());
            standEntity.addProjectile(vineGrab);
        }
    }

    @Override
    public void stoppedHolding(World world, LivingEntity user, IStandPower power, int ticksHeld, boolean willFire) {
        invokeForStand(power, stand -> {
            if (stand.getCurrentTaskAction() == this) {
                if (getLandedVineStand(user).isPresent()) {
                    return;
                } else {
                    stand.stopTaskWithRecovery();
                }
            }
        });
    }

    public static Optional<HPVineGrabEntity> getLandedVineStand(LivingEntity user) {
        List<HPVineGrabEntity> vineLanded = user.level.getEntitiesOfClass(HPVineGrabEntity.class,
                user.getBoundingBox().inflate(16), redBind -> user.is(redBind.getOwner()) && redBind.isAttachedToAnEntity());
        return !vineLanded.isEmpty() ? Optional.of(vineLanded.get(0)) : Optional.empty();
    }


    @Override
    public boolean noFinisherBarDecay() {
        return true;
    }


    @Override
    public boolean clHeldStartAnim(PlayerEntity user) {
        return AddonPlayerAnimations.grab.setWindupAnim(user);
    }



    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        super.onTaskStopped(world, standEntity, standPower, task, newAction);
        if(world.isClientSide){
            if(standPower.getUser() instanceof PlayerEntity) AddonPlayerAnimations.grab.stopAnim((PlayerEntity) standPower.getUser());
        }
    }
}
