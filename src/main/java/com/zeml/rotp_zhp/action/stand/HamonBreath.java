package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.playeranim.anim.ModPlayerAnimations;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.action.stand.projectile.HPGrabCommand;
import com.zeml.rotp_zhp.client.sound.ModClientTickingSoundsHelper;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicBoolean;

public class HamonBreath extends StandEntityAction {
    public HamonBreath(StandEntityAction.Builder builder){
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target) {
        boolean scarlet = INonStandPower.getNonStandPowerOptional(power.getUser())
                .map(ipower->ipower.getTypeSpecificData(ModPowers.HAMON.get())
                        .map(hamonData -> hamonData.isSkillLearned(ModHamonSkills.SCARLET_OVERDRIVE.get())
                                && hamonData.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get())).orElse(false)).orElse(false);
        HermitPurpleEntity HP = (HermitPurpleEntity) power.getStandManifestation();
        if(HP != null && scarlet && HPGrabCommand.getLandedVineStand(power.getUser()).isPresent()){
            return InitStands.HP_GRAB_SCARLET.get();
        }
        return this;
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
    protected boolean autoSummonStand(IStandPower power) {
        return false;
    }

    @Override
    public boolean canStaminaRegen(IStandPower standPower, StandEntity standEntity) {
        return true;
    }

    @Override
    public boolean enabledInHudDefault() {
        return false;
    }


    @Override
    public boolean clHeldStartAnim(PlayerEntity user) {
        return ModPlayerAnimations.hamonBreath.setAnimEnabled(user, true);
    }

    @Override
    public void clHeldStopAnim(PlayerEntity user) {
        ModPlayerAnimations.hamonBreath.setAnimEnabled(user, false);
    }


    @Override
    protected void playSoundAtStand(World world, StandEntity standEntity, SoundEvent sound, IStandPower standPower, Phase phase) {
        if (world.isClientSide()) {
            if (canBeCanceled(standPower, standEntity, phase, null)) {
                ModClientTickingSoundsHelper.playStandEntityCancelableActionSound(standEntity, sound, this, phase, 1.0F, 1.0F, false);
            }
            else {
                standEntity.playSound(sound, 1.0F, 1.0F, ClientUtil.getClientPlayer());
            }
        }
    }
}
