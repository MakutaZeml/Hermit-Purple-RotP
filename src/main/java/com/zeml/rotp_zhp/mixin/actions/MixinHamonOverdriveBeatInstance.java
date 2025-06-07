package com.zeml.rotp_zhp.mixin.actions;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonOverdriveBeat;
import com.github.standobyte.jojo.action.non_stand.HamonSunlightYellowOverdrive;
import com.github.standobyte.jojo.action.player.ContinuousActionInstance;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.mixin.actions.interfaces.HamonOverdriveBeatInstanceAccesor;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.standobyte.jojo.action.non_stand.HamonAction.addPointsForAction;

@Mixin(value = HamonOverdriveBeat.Instance.class, remap = false)
public class MixinHamonOverdriveBeatInstance extends ContinuousActionInstance<HamonOverdriveBeat, INonStandPower> {

    public MixinHamonOverdriveBeatInstance(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, HamonOverdriveBeat action) {
        super(user, userCap, playerPower, action);
    }

    @Inject(method = "punch", at = @At("HEAD") ,cancellable = true)
    private void onPunch(LivingEntity target, CallbackInfo ci){
        World world = user.level;

        RotpHermitPurpleAddon.LOGGER.debug("condicion {}", IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false));

        if(IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false)){
            if (!world.isClientSide()) {

                RotpHermitPurpleAddon.LOGGER.debug("Is this working? {}", user);

                IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {
                    HamonOverdriveBeat hamonAction = getAction();
                    if (MCUtil.isHandFree(user, Hand.OFF_HAND)) {
                        float damage = 3.0f;
                        float cost = hamonAction.getEnergyCost(playerPower, new ActionTarget(target));

                        HamonData userHamon = ((HamonOverdriveBeatInstanceAccesor)( HamonOverdriveBeat.Instance)(Object) this).getUserHamon();

                        float efficiency = userHamon.getActionEfficiency(cost, true, hamonAction.getUnlockingSkill());

                        if (StandHamonDamage.dealHamonDamage(target, damage * efficiency, user, null, standPower,1,1)) {
                            world.playSound(null, target.getX(), target.getEyeY(), target.getZ(), ModSounds.HAMON_SYO_PUNCH.get(), target.getSoundSource(), 1F, 1.5F);
                            target.knockback(1.25F, user.getX() - target.getX(), user.getZ() - target.getZ());
                            addPointsForAction(playerPower, userHamon, BaseHamonSkill.HamonStat.STRENGTH, cost, efficiency);
                            playerPower.consumeEnergy(cost);
                        }
                    }

                    HamonSunlightYellowOverdrive.doMeleeAttack(user, target);
                });

            }

            if (user instanceof PlayerEntity) {
                ((PlayerEntity) user).resetAttackStrengthTicker();
            }
            ci.cancel();
        }
    }



}
