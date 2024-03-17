package com.zeml.rotp_zhp.util;

import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.NonStandAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineEntity;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

import static com.github.standobyte.jojo.action.non_stand.HamonAction.addPointsForAction;


@Mod.EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID)
public class GameplayHandler {
    private static final float energyCost = 0;

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
                            }
                            event.setAmount(Math.max(event.getAmount() - 14.F / 4F, 0));
                        }
                    }
            );

        }
    }

}
