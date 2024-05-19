package com.zeml.rotp_zhp.util;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.init.power.stand.ModStands;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.damage.StandDamageSource;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                            if(ent instanceof LivingEntity){
                                LivingEntity liv = (LivingEntity) ent;
                                liv.hurt(DamageSource.GENERIC,1);

                                INonStandPower.getNonStandPowerOptional(target).ifPresent(ipower->{
                                    Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                                    if(hamonOp.isPresent()){
                                        HamonData hamon = hamonOp.get();

                                        float hamonDamage = hamon.getHamonStrengthLevel()/30F;

                                        if (hamon.isSkillLearned(ModHamonSkills.THROWABLES_INFUSION.get()) && ipower.getEnergy()>0){
                                            DamageUtil.dealHamonDamage(ent, hamonDamage, ent , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()));
                                        }
                                    }
                                });

                            }
                            if(dmgSource instanceof StandDamageSource){
                                double conf = JojoModConfig.getCommonConfigInstance(false).standDamageMultiplier.get();
                                event.setAmount(Math.max(event.getAmount() - (float) conf*14.F / 4F, 0));
                            } else{
                                event.setAmount(Math.max(event.getAmount() - 14.F / 4F, 0));
                            }

                        }
                    }
            );

        }
    }

}
