package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HPDoxx extends StandEntityAction {
    boolean once = true;
    public HPDoxx(StandEntityAction.Builder builder){
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
            byte scale = userPower.getUser().isShiftKeyDown()?(byte) 0:(byte) 2;
            BlockPos blockPos = null;
            String target = null;
            ItemStack itemStack = userPower.getUser().getItemInHand(Hand.OFF_HAND);
            if(((HermitPurpleEntity) standEntity).getMode() < 3){
                LivingEntity ent = HPHelperDox.HPGeneralObjectives(userPower.getUser(),(HermitPurpleEntity) standEntity);
                if(ent != null){
                    blockPos= ent.blockPosition();
                    target = ent.getName().getString();
                }
            }else {
                Structure<?> structure = HPHelperDox.HPStructure(userPower.getUser(),(HermitPurpleEntity) standEntity);
                if(structure != null){
                    ServerWorld serverWorld = (ServerWorld) world;
                    blockPos = serverWorld.getLevel().findNearestMapFeature(structure,standEntity.blockPosition(),100,false);
                    target = structure.getFeatureName();
                }
            }
            if(blockPos != null){
                itemStack.setCount(itemStack.getCount()-1);
                ServerWorld serverWorld = (ServerWorld) world;
                ItemStack stackMap = FilledMapItem.create(serverWorld, blockPos.getX(), blockPos.getZ(), scale , true, true);
                FilledMapItem.renderBiomePreviewMap(serverWorld, stackMap);
                MapData.addTargetDecoration(stackMap, blockPos, "+", MapDecoration.Type.RED_X);
                stackMap.setHoverName(new TranslationTextComponent("filled_map.divination").append(target));
                CompoundNBT nbt =stackMap.getOrCreateTagElement("display");
                nbt.putInt("MapColor",0x9E69CB);
                userPower.getUser().setItemInHand(Hand.OFF_HAND,itemStack);
                userPower.getUser().setItemInHand(Hand.MAIN_HAND,stackMap);
            }




        }
        once = true;
    }









}
