package com.zeml.rotp_zhp.util;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.damage.IndirectStandEntityDamageSource;
import com.github.standobyte.jojo.util.mc.damage.StandDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.HermitConfig;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPGrapplingVineEntity;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineBarrierEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import com.zeml.rotp_zhp.init.InitTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Mod.EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID)
public class GameplayHandler {



    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void blockDamage(LivingHurtEvent event){
        DamageSource dmgSource = event.getSource();
        LivingEntity target = event.getEntityLiving();
        if(!dmgSource.isExplosion()&&!dmgSource.isFire()&&!dmgSource.isMagic()&&!dmgSource.isProjectile()&& dmgSource.getDirectEntity() != null){
            Entity ent = dmgSource.getDirectEntity();
            IStandPower.getStandPowerOptional(target).ifPresent(
                    standPower ->{
                        if(standPower.getHeldAction()==InitStands.HP_BLOCK.get()){
                            StandEntity stand = getTargetStand(target);
                            double standDurability = stand.getDurability();
                            if(ent instanceof LivingEntity){
                                LivingEntity liv = (LivingEntity) ent;
                                liv.hurt(DamageSource.GENERIC,1);

                                INonStandPower.getNonStandPowerOptional(target).ifPresent(ipower->{
                                    Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                                    if(hamonOp.isPresent()){
                                        HamonData hamon = hamonOp.get();

                                        float hamonDamage = 1F;

                                        if (hamon.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get()) && ipower.getEnergy()>0){
                                            DamageUtil.dealHamonDamage(ent, hamonDamage, ent , target, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()));
                                            HermitPurpleEntity hermitPurple = MCUtil.entitiesAround(HermitPurpleEntity.class,target,5,false,hermitPurple1 -> hermitPurple1.getUser()==target).stream().findAny().orElse(null);
                                            if(hermitPurple != null){
                                                ent.hurt(new IndirectStandEntityDamageSource("hamon",null,hermitPurple),1);
                                            }
                                        }
                                    }
                                });

                            }
                            if(standDurability >0){
                                if(dmgSource instanceof StandDamageSource){
                                    double conf = JojoModConfig.getCommonConfigInstance(false).standDamageMultiplier.get();
                                    event.setAmount(Math.max(event.getAmount() - (float) conf* (float) standDurability / 4F, 0));
                                } else{
                                    event.setAmount(Math.max(event.getAmount() - (float) standDurability/ 4F, 0));
                                }
                            }
                        }
                    }
            );

        }
    }

    @SubscribeEvent(priority =  EventPriority.LOW)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;
            if(!player.level.isClientSide) {
                IStandPower.getStandPowerOptional(player).ifPresent(
                        power -> {
                            if(power.getType() == null && HermitConfig.getCommonConfigInstance(false).hamonToHermit.get()){
                                INonStandPower.getNonStandPowerOptional(player).ifPresent(ipower->{
                                    Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                                    if(hamonOp.isPresent()){
                                        HamonData hamon = hamonOp.get();
                                        if(hamon.getHamonStrengthLevel()==HermitConfig.getCommonConfigInstance(false).strength.get()&&
                                        hamon.getBreathingLevel() == HermitConfig.getCommonConfigInstance(false).breathing.get() &&
                                        hamon.getHamonControlLevel() == HermitConfig.getCommonConfigInstance(false).control.get()){
                                            power.givePower(InitStands.STAND_HERMITO_PURPLE.getStandType());
                                        }
                                    }
                                });
                            }
                        }
                );
            }

    }


    @Nullable
    private static StandEntity getTargetStand(LivingEntity target) {
        return IStandPower.getStandPowerOptional(target).map(stand -> {
            return Optional.ofNullable(stand.getStandManifestation() instanceof StandEntity ? (StandEntity) stand.getStandManifestation() : null);
        }).orElse(Optional.empty()).orElse(null);
    }

}
