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
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.HermitConfig;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.capability.LivingData;
import com.zeml.rotp_zhp.capability.LivingDataProvider;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPGrapplingVineEntity;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineBarrierEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitItems;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import com.zeml.rotp_zhp.init.InitTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
                                            StandHamonDamage.dealHamonDamage(ent, hamonDamage, target , target, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()),standPower,.75F,1);
                                            HermitPurpleEntity hermitPurple = (HermitPurpleEntity) standPower.getStandManifestation();
                                            if(hermitPurple != null){
                                                ent.hurt(new StandEntityDamageSource("hamon",target ,standPower),1);
                                            }
                                        }
                                    }
                                });

                            }
                            if(standDurability >0){
                                if(dmgSource instanceof StandDamageSource){
                                    double conf = JojoModConfig.getCommonConfigInstance(false).standDamageMultiplier.get();
                                    event.setAmount(Math.max(event.getAmount() -3F* (float) conf* (float) standDurability / 8F, 0));
                                } else{
                                    event.setAmount(Math.max(event.getAmount() - 3*(float) standDurability/ 8F, 0));
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
                                        if(hamon.getHamonStrengthLevel()>=HermitConfig.getCommonConfigInstance(false).strength.get()&&
                                        hamon.getBreathingLevel() >= HermitConfig.getCommonConfigInstance(false).breathing.get() &&
                                        hamon.getHamonControlLevel() >= HermitConfig.getCommonConfigInstance(false).control.get()){
                                            LazyOptional<LivingData> playerDataOptional = player.getCapability(LivingDataProvider.CAPABILITY);
                                            playerDataOptional.ifPresent(playerData ->{
                                                if(!playerData.isTriedHermit()){
                                                    power.givePower(InitStands.STAND_HERMITO_PURPLE.getStandType());
                                                    playerData.setTriedHermit(true);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                );
            }

    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCloneEvent(PlayerEvent.Clone event){
        if(!event.getEntityLiving().level.isClientSide){
            boolean tried = event.getOriginal().getCapability(LivingDataProvider.CAPABILITY).map(LivingData::isTriedHermit).orElse(false);
            event.getPlayer().getCapability(LivingDataProvider.CAPABILITY).ifPresent(livingData -> livingData.setTriedHermit(tried));
        }
    }

    @Nullable
    private static StandEntity getTargetStand(LivingEntity target) {
        return IStandPower.getStandPowerOptional(target).map(stand -> {
            return Optional.ofNullable(stand.getStandManifestation() instanceof StandEntity ? (StandEntity) stand.getStandManifestation() : null);
        }).orElse(Optional.empty()).orElse(null);
    }



    // ======================================== The Emperor ========================================


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){

        if(!event.getEntity().level.isClientSide){
            Entity entity = event.getEntity();
            if(entity instanceof ItemEntity){
                ItemEntity entItem = (ItemEntity) entity;
                if(entItem.getItem().getItem() == InitItems.EMPEROR.get()){
                    entity.remove();
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEmpPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;
        if(!player.level.isClientSide){
            IStandPower.getStandPowerOptional(player).ifPresent(
                    standPower -> {
                        StandType<?> emp = InitStands.STAND_EMPEROR.getStandType();
                        if(standPower.getType()!=emp){
                            empInv(player);
                        }else if (standPower.getStandManifestation() instanceof StandEntity){
                            if(player.getItemInHand(Hand.MAIN_HAND).getItem() != InitItems.EMPEROR.get() && player.getItemInHand(Hand.OFF_HAND).getItem() != InitItems.EMPEROR.get()){
                                ItemStack hand = player.getItemInHand(Hand.MAIN_HAND);
                                if(!hand.isEmpty()){
                                    ItemEntity ent = new ItemEntity(player.level,player.getX(),player.getY(),player.getZ(),hand);
                                    player.level.addFreshEntity(ent);
                                }

                                ItemStack itemStack = new ItemStack(InitItems.EMPEROR.get(),1);
                                CompoundNBT nbt =itemStack.getOrCreateTag();
                                player.getCapability(LivingDataProvider.CAPABILITY).ifPresent(livingData -> {
                                    nbt.putInt("mode",livingData.getMode());
                                });
                                player.setItemInHand(Hand.MAIN_HAND,itemStack);
                                oneEmp(player);
                            }

                            dupEmp(player);


                        }else {
                            empInv(player);
                        }
                    }
            );
        }


    }


    private static void empInv(PlayerEntity player){
        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack inventoryStack = player.inventory.getItem(i);
            if (inventoryStack.getItem() == InitItems.EMPEROR.get()) {
                inventoryStack.shrink(inventoryStack.getCount());
            }
        }
    }


    private static void oneEmp(PlayerEntity player) {

        int selected = player.inventory.selected;

        for (int i = 9; i < player.inventory.getContainerSize(); ++i){
            ItemStack inventoryStack = player.inventory.getItem(i);
            if (inventoryStack.getItem() == InitItems.EMPEROR.get()) {
                inventoryStack.shrink(inventoryStack.getCount());
            }
        }
        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack inventoryStack = player.inventory.getItem(i);
            if (inventoryStack.getItem() == InitItems.EMPEROR.get()) {

                if (i!=selected){
                    inventoryStack.shrink(inventoryStack.getCount());
                }
            }
        }
    }


    private static void dupEmp(PlayerEntity player){
        int count = 0;
        ArrayList<Integer> places = new ArrayList<>();

        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack inventoryStack = player.inventory.getItem(i);
            if (inventoryStack.getItem() == InitItems.EMPEROR.get()) {
                ++count;
                places.add((Integer) i);
            }
            if(count>1){
                for (Integer in:places) {
                    int pl = (int) in;
                    ItemStack dupliEmp = player.inventory.getItem(pl);
                    dupliEmp.shrink(1);
                }
            }
        }

    }

}
