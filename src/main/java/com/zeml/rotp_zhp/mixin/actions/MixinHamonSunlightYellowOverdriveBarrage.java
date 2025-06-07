package com.zeml.rotp_zhp.mixin.actions;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonSunlightYellowOverdrive;
import com.github.standobyte.jojo.action.non_stand.HamonSunlightYellowOverdriveBarrage;
import com.github.standobyte.jojo.action.player.ContinuousActionInstance;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.client.playeranim.anim.ModPlayerAnimations;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.ability_specific.TrSYOBarrageFinisherPacket;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.mixin.actions.interfaces.HamonSunlightYellowOverdriveAccesor;
import com.zeml.rotp_zhp.mixin.actions.interfaces.HamonSunlightYellowOverdriveBarrageAccessor;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HamonSunlightYellowOverdriveBarrage.class, remap = false)
public class MixinHamonSunlightYellowOverdriveBarrage {


    @Mixin(value = HamonSunlightYellowOverdriveBarrage.Instance.class, remap = false)
    public abstract static class MixinInstance extends ContinuousActionInstance<HamonSunlightYellowOverdriveBarrage, INonStandPower> {
        private final int MAX_BARRAGE_DURATION = ((HamonSunlightYellowOverdriveBarrageAccessor) (HamonSunlightYellowOverdriveBarrage.Instance) (Object) this).getMAX_BARRAGE_DURATION();
        private boolean finishingPunch = ((HamonSunlightYellowOverdriveBarrageAccessor) (HamonSunlightYellowOverdriveBarrage.Instance) (Object) this).getFinishingPunch();;
        public MixinInstance(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, HamonSunlightYellowOverdriveBarrage action) {
            super(user, userCap, playerPower, action);
        }

        @Shadow
        public abstract void startFinishingPunch();

        @Inject(method = "playerTick", at = @At("HEAD"), cancellable = true)
        protected void playerTick(CallbackInfo ci) {
            if (IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false)) {
                IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {
                    LivingEntity user = getUser();
                    World world = user.level;
                    int tick = getTick();
                    if (tick < MAX_BARRAGE_DURATION) {
                        // barrage tick
                        ActionTarget target = getPower().getMouseTarget();
                        LivingEntity targetEntity = null;
                        switch (target.getType()) {
                            case BLOCK:
                                BlockPos pos = target.getBlockPos();
                                if (!world.isClientSide() && JojoModUtil.canEntityDestroy((ServerWorld) world, pos, world.getBlockState(pos), user)) {
                                    if (!world.isEmptyBlock(pos)) {
                                        BlockState blockState = world.getBlockState(pos);
                                        float digDuration = blockState.getDestroySpeed(world, pos);
                                        boolean dropItem = true;
                                        if (user instanceof PlayerEntity) {
                                            PlayerEntity player = (PlayerEntity) user;
                                            digDuration /= player.getDigSpeed(blockState, pos);
                                            if (player.abilities.instabuild) {
                                                digDuration = 0;
                                                dropItem = false;
                                            } else if (!ForgeHooks.canHarvestBlock(blockState, player, world, pos)) {
                                                digDuration *= 10F / 3F;
//                                    dropItem = false;
                                            }
                                        }
                                        if (digDuration >= 0 && digDuration <= 2.5F * Math.sqrt(user.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                                            MCUtil.destroyBlock(world, pos, dropItem, user);
                                        } else {
                                            SoundType soundType = blockState.getSoundType(world, pos, user);
                                            world.playSound(null, pos, soundType.getHitSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F);
                                        }
                                    }
                                }
                                break;
                            case ENTITY:
                                if (target.getEntity() instanceof LivingEntity) {
                                    targetEntity = (LivingEntity) target.getEntity();
                                    targetEntity.addEffect(new EffectInstance(ModStatusEffects.IMMOBILIZE.get(), 10, 0, false, false, false));
                                    if (user instanceof PlayerEntity) {
                                        int invulTicks = targetEntity.invulnerableTime;
                                        ((PlayerEntity) user).attack(targetEntity);
                                        targetEntity.invulnerableTime = invulTicks;
                                    }
                                    if (!world.isClientSide()) {
                                        StandHamonDamage.dealHamonDamage(targetEntity, 0.1F, user, null,
                                                attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_YELLOW.get()), standPower, 1F, 1F);
                                        if (targetEntity.getHealth() < 2) {
                                            startFinishingPunch();
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }

                        if (world.isClientSide() && tick % 2 == 0) {
                            user.swinging = false;
                            user.swing(tick % 4 == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND);
                        }
                    } else if (!world.isClientSide() && tick == MAX_BARRAGE_DURATION) {
                        startFinishingPunch();
                    }
                });
                ci.cancel();
            }
        }

        @Inject(method = "startFinishingPunch", at = @At("HEAD"), cancellable = true)
        protected void startFinishingPunch(CallbackInfo ci) {
            if (IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false)) {
                IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {
                    LivingEntity user = getUser();
                    INonStandPower power = getPower();
                    if (!finishingPunch) {
                        finishingPunch = true;
                        tick = MAX_BARRAGE_DURATION;
                        World world = user.level;

                        ActionTarget target = power.getMouseTarget();
                        Entity entity = target.getEntity();
                        if (entity instanceof LivingEntity) {
                            LivingEntity targetEntity = (LivingEntity) entity;
                            targetEntity.removeEffect(ModStatusEffects.IMMOBILIZE.get());

                            PlayerEntity playerUser = user instanceof PlayerEntity ? ((PlayerEntity) user) : null;
                            if (playerUser != null) {
                                CommonReflection.setAttackStrengthTicker(user, MathHelper.ceil(playerUser.getCurrentItemAttackStrengthDelay()));
                            }

                            if (!world.isClientSide()) {
                                HamonData hamon = power.getTypeSpecificData(ModPowers.HAMON.get()).get();
                                float efficiency = hamon.getActionEfficiency(0, false, ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE_BARRAGE.get());

                                float damage = 15F;
                                damage *= efficiency;

                                if (StandHamonDamage.dealHamonDamage(targetEntity, damage, user, null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_YELLOW.get()),standPower,1,1)) {
                                    world.playSound(null, targetEntity.getX(), targetEntity.getEyeY(), targetEntity.getZ(), ModSounds.HAMON_SYO_PUNCH.get(), targetEntity.getSoundSource(), 1.0F, 1.0F);
                                    DamageUtil.knockback3d(targetEntity, 2F, user.xRot, user.yRot);
                                    if (hamon.isSkillLearned(ModHamonSkills.HAMON_SPREAD.get())) {
                                        targetEntity.addEffect(new EffectInstance(ModStatusEffects.HAMON_SPREAD.get(), 200, 3));
                                    }
                                }
                            }

                            if (playerUser != null) {
                                playerUser.attack(targetEntity);
                            }
                            else if (!world.isClientSide()) {
                                user.doHurtTarget(targetEntity);
                            }

                            if (world.isClientSide()) {
                                user.swing(Hand.MAIN_HAND);
                            }
                        }

                        if (!world.isClientSide()) {
                            PacketManager.sendToClientsTrackingAndSelf(new TrSYOBarrageFinisherPacket(user.getId()), (ServerPlayerEntity) user);
                        }
                        else if (user instanceof PlayerEntity) {
                            PlayerEntity player = (PlayerEntity) user;
                            ModPlayerAnimations.playerBarrageAnim.setAnimEnabled(player, false);
                            ModPlayerAnimations.syoBarrage.setFinisherAnim(player);
                        }
                    }
                });
                ci.cancel();
            }


        }

    }
}