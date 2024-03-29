package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.ImageTaker;
import com.zeml.rotp_zhp.init.InitSounds;
import de.maxhenkel.camera.Main;
import de.maxhenkel.camera.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HPCamera extends StandEntityAction {
    boolean once = true;

    public HPCamera(Builder builder){
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target){
        if(user instanceof PlayerEntity){
            if(!findPaper((PlayerEntity) user).isEmpty()){
                return ActionConditionResult.POSITIVE;
            }
            return conditionMessage("no_paper");
        }
        return ActionConditionResult.NEGATIVE;
    }


    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide){
            if (standEntity.getUser() instanceof  PlayerEntity){
                LivingEntity ent = HPDoxx.hpObj(userPower.getUser());
                if(ent != null){
                    Minecraft.getInstance().execute(()->
                            ImageTaker.takeScreenshot(UUID.randomUUID())
                            );
                    standEntity.playSound(ModSounds.TAKE_IMAGE,1,1);
                }
            }

        }
        once = true;
    }

    @Override
    public void standTickWindup(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if(once){
            LivingEntity ent = HPDoxx.hpObj(userPower.getUser());
            if (ent != null) {
                standEntity.playSound(InitSounds.HERMITO_PURPLE_SUMMON.get(), 1, 1);
                standEntity.playSound(InitSounds.USER_HP.get(), 1, 1);
                once=false;
            }
        }

    }



    private static List<ItemStack> findPaper(PlayerEntity player) {
        List<ItemStack> items = new ArrayList<>();
        if (isPaper(player.getItemInHand(Hand.MAIN_HAND))) {
            items.add(player.getItemInHand(Hand.MAIN_HAND));
        }
        if (isPaper(player.getItemInHand(Hand.OFF_HAND))) {
            items.add(player.getItemInHand(Hand.OFF_HAND));
        }
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack itemstack = player.inventory.getItem(i);

            if (isPaper(itemstack)) {
                items.add(itemstack);
            }
        }
        return items;
    }

    protected static boolean isPaper(ItemStack stack) {
        return stack.getItem().is(Main.SERVER_CONFIG.cameraConsumeItem);
    }

}
