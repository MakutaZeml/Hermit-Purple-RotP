package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.util.mc.MCUtil;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HPDoxx extends StandEntityAction {
    public HPDoxx(StandEntityAction.Builder builder){
        super(builder);
    }


    @Nullable
    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target) {
        if(power.getUser().getItemInHand(Hand.OFF_HAND).getItem() == ModItems.POLAROID.get()){
            return InitStands.HP_CAMERA.get();
        }
        return super.replaceAction(power, target);
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
                    blockPos = serverWorld.getLevel().findNearestMapFeature(structure,standEntity.blockPosition(),100,false);
                    target = structure.getFeatureName();
                }
            } else if (((HermitPurpleEntity) standEntity).getMode() == 4) {
                blockPos = HPHelperDox.biomesPos(standEntity,((HermitPurpleEntity) standEntity).getTarget(),(ServerWorld) world);
                target = giveString((HermitPurpleEntity) standEntity);
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
    }



    @Override
    public IFormattableTextComponent getTranslatedName(IStandPower power, String key) {
        HermitPurpleEntity hm = getStand((PlayerEntity) power.getUser());
        if(hm != null) {
            if(hm.getMode() == 1){
                return new TranslationTextComponent(key+".s").append(hm.getTarget());
            } else if (hm.getMode() == -1) {
                TranslationTextComponent user = new TranslationTextComponent("action.rotp_zhp.hp_doxx_user");
                return new TranslationTextComponent(key+".s").append(StandUtil.availableStands(!power.getUser().level.isClientSide).filter(standType -> standType.getRegistryName().toString().equals(hm.getTarget())).findFirst().orElse(null).getName()).append(user);
            }else if(hm.getMode() > 1){
                String target = giveString(hm);
                return new TranslationTextComponent(key+".s").append(target);
            }
        }
        return super.getTranslatedName(power, key);
    }

    private HermitPurpleEntity getStand(PlayerEntity player){
        Optional<HermitPurpleEntity> hm = MCUtil.entitiesAround(HermitPurpleEntity.class, player, 5, false, HermitPurpleEntity::isAlive).stream()
                .filter(entity -> entity.getUser() == player).findAny();
        return hm.orElse(null);
    }

    public static String giveString(HermitPurpleEntity hermitPurple){
        if(hermitPurple.getMode() == 2){
            return ForgeRegistries.ENTITIES.getValues().stream().filter(entityType -> entityType.getRegistryName().toString().equals(hermitPurple.getTarget())).findAny().get().getDescription().getString();
        } else if (hermitPurple.getMode() == 4) {
            return biomeName(ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome.getRegistryName().toString().equals(hermitPurple.getTarget())).findAny().orElse(null)).getString();
        } else {
            return ForgeRegistries.STRUCTURE_FEATURES.getValues().stream().filter(structure -> structure.getRegistryName().toString().equals(hermitPurple.getTarget())).findAny().get().getFeatureName();
        }
    }

    public static TranslationTextComponent biomeName(Biome item){
        return new TranslationTextComponent(Util.makeDescriptionId("biome",item.getRegistryName()));
    }

    @Override
    public StandAction[] getExtraUnlockable() {
        return new StandAction[]{InitStands.HP_CAMERA.get()};
    }
}
