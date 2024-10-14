package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.PhotoForOtherPlayerPacket;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.ObjectWrapper;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class HPCamera extends StandEntityAction {
    public HPCamera(StandEntityAction.Builder builder){
        super(builder);
    }


    /*
    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target){
        return hasPaper(power.getUser(),false)?ActionConditionResult.POSITIVE:conditionMessage("polaroid.paper");
    }

     */

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
                
                float level = (float) Math.random() * userPower.getResolveLevel();
                float random = (float) Math.random() * 3.5F;
                if (random > level) {
                    ItemStack cameraItem = cameraUser.getItemInHand(Hand.OFF_HAND);
                    cameraItem.shrink(1);
                    cameraUser.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                }
            }
        }

    }


/*
    @Override
    public void standTickRecovery(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if(world.isClientSide){
            PolaroidHelper.takePicture(Vector3d.atCenterOf(blockPos),null,true,userPower.getUser().getId());
        }
        if(!world.isClientSide){
            GameplayHandler.hermitManual.remove(userPower.getUser());
            standEntity.moveTo(userPower.getUser().position());
            standEntity.remove();
            userPower.getType().summon(userPower.getUser(), userPower,true);
        }
    }


    @Override
    public void standTickWindup(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if(!world.isClientSide){
            GameplayHandler.hermitManual.add(userPower.getUser());
            if(((HermitPurpleEntity) standEntity).getMode() < 3){
                LivingEntity ent = HPHelperDox.HPGeneralObjectives(userPower.getUser(),(HermitPurpleEntity) standEntity);
                if(ent != null){
                    this.blockPos= ent.blockPosition();
                    if(userPower.getUser().hasEffect(ModStatusEffects.RESOLVE.get())){
                        INonStandPower.getNonStandPowerOptional(userPower.getUser()).ifPresent(ipower ->{
                            if (ipower.getType() == ModPowers.HAMON.get()){
                                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                                HamonData hamon = hamonOp.get();
                                if(hamon.isSkillLearned(ModHamonSkills.DETECTOR.get())){
                                    int seconds = Objects.requireNonNull(userPower.getUser().getEffect(ModStatusEffects.RESOLVE.get())).getDuration();
                                    ent.addEffect(new EffectInstance(Effects.GLOWING,seconds/4));
                                }
                            }
                        });

                    }
                }
            }else if(((HermitPurpleEntity) standEntity).getMode() == 3){
                Structure<?> structure = HPHelperDox.HPStructure(userPower.getUser(),(HermitPurpleEntity) standEntity);
                if(structure != null){
                    ServerWorld serverWorld = (ServerWorld) world;
                    this.blockPos = serverWorld.getLevel().findNearestMapFeature(structure,standEntity.blockPosition(),100,false);
                }
            } else if (((HermitPurpleEntity) standEntity).getMode() == 4) {
                this.blockPos = HPHelperDox.biomesPos(standEntity,((HermitPurpleEntity) standEntity).getTarget(),(ServerWorld) world);
            }
        }

     */

    @Override
    public void overrideVanillaMouseTarget(ObjectWrapper<ActionTarget> targetContainer, World world, LivingEntity user, IStandPower power) {
        super.overrideVanillaMouseTarget(targetContainer, world, user, power);
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
