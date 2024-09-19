package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.polaroid.PolaroidHelper;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.init.power.stand.ModStands;
import com.github.standobyte.jojo.item.PolaroidItem;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.util.general.ObjectWrapper;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.power.impl.stand.type.HermitPurpleStandType;
import com.zeml.rotp_zhp.util.GameplayHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HPCamera extends StandEntityAction {
    private BlockPos blockPos;
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
        if(stand instanceof HermitPurpleEntity){
            HermitPurpleEntity hm = (HermitPurpleEntity) stand;
            if(hm.getMode() == 1 || hm.getMode() ==-1){
                return hasPaper(power.getUser(),false)?ActionConditionResult.POSITIVE:conditionMessage("polaroid.paper");
            }
            return conditionMessage("no_player_target");
        }
        return ActionConditionResult.NEGATIVE;
    }



    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide){
            if(((HermitPurpleEntity) standEntity).getMode() < 3){
                LivingEntity ent = HPHelperDox.HPGeneralObjectives(userPower.getUser(),(HermitPurpleEntity) standEntity);
                System.out.println(ent);
                if(ent instanceof PlayerEntity){
                    GameplayHandler.photo.put((PlayerEntity) ent, userPower.getUser());
                    System.out.println(GameplayHandler.photo);
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
    public void afterClick(World world, LivingEntity user, IStandPower power, boolean passedRequirements) {
        float level = (float) Math.random()*power.getResolveLevel();
        float random = (float) Math.random()*3.5F;
        if(random > level){
            if(!world.isClientSide) {
                user.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            }else {
                if(user instanceof PlayerEntity){
                    user.level.playSound( (PlayerEntity) user,user.blockPosition(), SoundEvents.ITEM_BREAK, SoundCategory.PLAYERS,1F,1F);
                }
            }

        }
        super.afterClick(world, user, power, passedRequirements);
    }


    @Override
    public void overrideVanillaMouseTarget(ObjectWrapper<ActionTarget> targetContainer, World world, LivingEntity user, IStandPower power) {
        super.overrideVanillaMouseTarget(targetContainer, world, user, power);
    }

    private boolean hasPaper(LivingEntity user, boolean delete){
        boolean value = true;
        if(user instanceof PlayerEntity){
            value = false;
            PlayerEntity cameraPlayer = (PlayerEntity) user;
            for(int i = 0; i < cameraPlayer.inventory.getContainerSize(); ++i) {
                ItemStack item = cameraPlayer.inventory.getItem(i);
                if (!item.isEmpty() && item.getItem() == Items.PAPER) {
                    value = true;
                    if(delete){
                        item.shrink(1);
                    }
                    break;
                }
            }
        }
       return value;
    }
}
