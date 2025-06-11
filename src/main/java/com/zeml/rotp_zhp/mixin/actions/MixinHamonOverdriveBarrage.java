package com.zeml.rotp_zhp.mixin.actions;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonAction;
import com.github.standobyte.jojo.action.non_stand.HamonOverdriveBarrage;
import com.github.standobyte.jojo.entity.stand.StandStatFormulas;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.HermitConfig;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HamonOverdriveBarrage.class, remap = false)
public abstract class MixinHamonOverdriveBarrage extends HamonAction {

    public MixinHamonOverdriveBarrage(HamonAction.Builder builder){
        super(builder);
    }


    @Shadow public static void attack(PlayerEntity attacker, Entity target, float damage, boolean sharpnessParticles){};

    @Inject(method = "holdTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;ILcom/github/standobyte/jojo/action/ActionTarget;Z)V", at = @At("HEAD"), cancellable = true)
    private void holdTick(World world, LivingEntity user, INonStandPower power, int ticksHeld, ActionTarget target, boolean requirementsFulfilled, CallbackInfo ci){

        if(IStandPower.getStandPowerOptional(user).map(standPower -> standPower.getStandManifestation() instanceof HermitPurpleEntity).orElse(false) &&
                HermitConfig.getCommonConfigInstance(false).hermitHamon.get()) {
            IStandPower.getStandPowerOptional(user).ifPresent(standPower -> {

                RotpHermitPurpleAddon.LOGGER.debug("FUNCIONA? {}", standPower);

                if (requirementsFulfilled) {
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
                                        }
                                        else if (!ForgeHooks.canHarvestBlock(blockState, player, world, pos)) {
                                            digDuration *= 10F / 3F;
//                                dropItem = false;
                                        }
                                    }
                                    if (digDuration >= 0 && digDuration <= 2.5F * Math.sqrt(user.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                                        MCUtil.destroyBlock(world, pos, dropItem, user);
                                        power.getTypeSpecificData(ModPowers.HAMON.get()).get().hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH, getHeldTickEnergyCost(power));
                                    }
                                    else {
                                        SoundType soundType = blockState.getSoundType(world, pos, user);
                                        world.playSound(null, pos, soundType.getHitSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F);
                                    }
                                }
                            }
                            break;
                        case ENTITY:
                            Entity targetEntity = target.getEntity();

                            double atkAttribute = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
                            double enchBonus;
                            if (targetEntity instanceof LivingEntity) {
                                enchBonus = EnchantmentHelper.getDamageBonus(user.getMainHandItem(), ((LivingEntity) targetEntity).getMobType());
                            } else {
                                enchBonus = EnchantmentHelper.getDamageBonus(user.getMainHandItem(), CreatureAttribute.UNDEFINED);
                            }
                            atkAttribute += enchBonus;
                            double strength = atkAttribute + 8;
                            double speed = user.getAttributeValue(Attributes.ATTACK_SPEED) + 8;
                            float damage = StandStatFormulas.getBarrageHitDamage(strength, 0) * StandStatFormulas.getBarrageHitsPerSecond(speed) / 20;

                            if (user instanceof PlayerEntity) {
                                int invulTicks = targetEntity.invulnerableTime;
                                attack((PlayerEntity) user, targetEntity, damage, enchBonus > 0);
                                targetEntity.invulnerableTime = invulTicks;
                            }
                            if (!world.isClientSide()) {
                                StandHamonDamage.dealHamonDamage(targetEntity, 0.1F, user, null, null,standPower,1,1);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

            ci.cancel();
        }

    }


}
