package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.PhotoForOtherPlayerPacket;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.util.general.ObjectWrapper;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Optional;

public class HPCamera extends StandEntityAction {
    public HPCamera(StandEntityAction.Builder builder){
        super(builder);
    }



    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        if (stand instanceof HermitPurpleEntity) {
            HermitPurpleEntity hm = (HermitPurpleEntity) stand;
            if (hm.getMode() == 1 || hm.getMode() == -1) {
                if (hasPaper(power.getUser(), false)) {
                    return ActionConditionResult.POSITIVE;
                }
                else {
                    return ActionConditionResult.createNegative(new TranslationTextComponent("jojo.polaroid.paper"));
                }
            }
            return conditionMessage("no_player_target");
        }
        return ActionConditionResult.NEGATIVE;
    }



    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if (!world.isClientSide) {
            LivingEntity cameraUser = standEntity.getUser();
            
            LivingEntity selectedTarget = HPHelperDox.HPGeneralObjectives(cameraUser, (HermitPurpleEntity) standEntity);
            if (selectedTarget instanceof ServerPlayerEntity && hasPaper(cameraUser, true)) {
                ServerPlayerEntity playerToDox = (ServerPlayerEntity) selectedTarget;
                PacketManager.sendToClient(new PhotoForOtherPlayerPacket(cameraUser.getId()), playerToDox);
                
                float level = userPower.getResolveLevel();
                float random = (float) Math.random() * 3.5F;
                System.out.println(random);
                if (random > level) {
                    ItemStack cameraItem = cameraUser.getItemInHand(Hand.OFF_HAND);
                    cameraItem.shrink(1);
                    cameraUser.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                }
            }
        }

    }


    @Override
    public IFormattableTextComponent getTranslatedName(IStandPower power, String key) {
        HermitPurpleEntity hm = (HermitPurpleEntity) power.getStandManifestation();
        if(hm != null && hm.getMode() != 0){
            TranslationTextComponent name= new TranslationTextComponent(hm.getTarget());
            if(hm.getMode() == -1){
                name = (TranslationTextComponent) StandUtil.availableStands(!power.getUser().level.isClientSide).filter(standType -> standType.getRegistryName().toString().equals(hm.getTarget())).findFirst().orElse(null).getName();
                return new TranslationTextComponent(key+".stand",name);
            }
            return new TranslationTextComponent(key+".s",name);


        }
        return super.getTranslatedName(power, key);
    }



    private boolean hasPaper(LivingEntity user, boolean delete) {
        boolean value = true;
        if (user instanceof PlayerEntity) {
            value = false;
            PlayerEntity cameraPlayer = (PlayerEntity) user;
            if (cameraPlayer.abilities.instabuild) {
                return true;
            }
            for (int i = 0; i < cameraPlayer.inventory.getContainerSize(); ++i) {
                ItemStack item = cameraPlayer.inventory.getItem(i);
                if (!item.isEmpty() && item.getItem() == Items.PAPER) {
                    value = true;
                    if (delete) {
                        item.shrink(1);
                    }
                    break;
                }
            }
        }
        return value;
    }



}
