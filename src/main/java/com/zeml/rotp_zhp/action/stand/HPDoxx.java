package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.init.InitSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HPDoxx extends StandEntityAction {
    public HPDoxx(Builder builder){
        super(builder);
    }


    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target){
        ItemStack itemStack = user.getItemInHand(Hand.OFF_HAND);
        if(itemStack.getItem() == Items.MAP){
            if(user.getMainHandItem().isEmpty()){
                return ActionConditionResult.POSITIVE;
            }
            return conditionMessage("hand");
        }
        return conditionMessage("no_map");
    }


    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide){

            LivingEntity ent = hpObj(userPower.getUser());
            if(ent != null){
                ItemStack itemStack = userPower.getUser().getItemInHand(Hand.OFF_HAND);
                itemStack.setCount(itemStack.getCount()-1);
                BlockPos blockPos= ent.blockPosition();
                ServerWorld serverWorld = (ServerWorld) world;
                ItemStack stackMap = FilledMapItem.create(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                FilledMapItem.renderBiomePreviewMap(serverWorld, stackMap);
                MapData.addTargetDecoration(stackMap, blockPos, "+", MapDecoration.Type.RED_X);
                stackMap.setHoverName(new TranslationTextComponent("filled_map.divination").append(ent.getName()));
                userPower.getUser().setItemInHand(Hand.OFF_HAND,itemStack);
                userPower.getUser().setItemInHand(Hand.MAIN_HAND,stackMap);
                standEntity.playSound(InitSounds.HERMITO_PURPLE_SUMMON.get(),1,1);
                standEntity.playSound(InitSounds.USER_HP.get(),1,1);
            }

        }
    }

    public LivingEntity HPojectives(LivingEntity user){
        World world =user.level;
        List<LivingEntity> lista =  world.getEntitiesOfClass(LivingEntity.class,user.getBoundingBox().inflate(1000), EntityPredicates.ENTITY_STILL_ALIVE).stream()
                .filter(entity -> entity != user)
                .filter(entity -> entity instanceof  PlayerEntity || entity.getMaxHealth()>100).collect(Collectors.toList());
        LivingEntity fin = null;
        if(!lista.isEmpty()){
            int n = lista.size();
            int i = MathHelper.floor(n*Math.random());
            fin = lista.get(i);
        }
        return fin;
    }

    public LivingEntity hpObj(LivingEntity user){
        if(user instanceof ServerPlayerEntity){
            ServerPlayerEntity player = Objects.requireNonNull(user.getServer()).getPlayerList().getPlayer(user.getUUID());
            ServerWorld world= player.getLevel();

            List<Entity> lista =  world.getEntities().filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity != user)
                    .filter(entity -> entity instanceof  PlayerEntity || ((LivingEntity)entity).getMaxHealth()>100).collect(Collectors.toList());
            LivingEntity fin = null;
            if(!lista.isEmpty()){
                int n = lista.size();
                int i = MathHelper.floor(n*Math.random());
                fin = (LivingEntity) lista.get(i);
            }
            return fin;

        }
        return HPojectives(user);
    }

}
