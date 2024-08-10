package com.zeml.rotp_zhp.entity.stand.stands;


import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandRelativeOffset;
import com.github.standobyte.jojo.entity.stand.StandEntityType;

import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.zeml.rotp_zhp.action.stand.projectile.HPAttachBarrier;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineBarrierEntity;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.util.HPBarriersNet;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class HermitPurpleEntity extends StandEntity {

    private static final DataParameter<Integer> MODE = EntityDataManager.defineId(HermitPurpleEntity.class,DataSerializers.INT);
    private static final DataParameter<String> TARGET = EntityDataManager.defineId(HermitPurpleEntity.class,DataSerializers.STRING);
    private static final DataParameter<Integer> PLACED_BARRIERS = EntityDataManager.defineId(HermitPurpleEntity.class, DataSerializers.INT);
    private HPBarriersNet placedBarriers = new HPBarriersNet();
    private HPVineBarrierEntity stringToUser;

    private HPVineBarrierEntity stringFromStand;



    public HermitPurpleEntity(StandEntityType<HermitPurpleEntity> type, World world){
        super(type, world);
        unsummonOffset = getDefaultOffsetFromUser().copy();
    }


    @Override
    public void tick(){
        if (!level.isClientSide()) {
            placedBarriers.tick();
            setPlacedBarriersCount(placedBarriers.getSize());
        }
        super.tick();
    }

    public HPBarriersNet getBarriersNet() {
        return placedBarriers;
    }

    public boolean canPlaceBarrier() {
        return getPlacedBarriersCount() < HPAttachBarrier.getMaxBarriersPlaceable(getUserPower());
    }

    public boolean hasBarrierAttached() {
        return getPlacedBarriersCount() > 0 ||
                stringFromStand != null && stringFromStand.isAlive() && stringFromStand != stringToUser;
    }

    public void attachBarrier(BlockPos blockPos) {
        if (!level.isClientSide()) {
            if (!canPlaceBarrier()) {
                return;
            }
            if (stringFromStand != null && stringFromStand.isAlive()) {
                if (blockPos.equals(stringFromStand.getOriginBlockPos())) {
                    return;
                }
                stringFromStand.attachToBlockPos(blockPos);
                placedBarriers.add(stringFromStand);
                setPlacedBarriersCount(placedBarriers.getSize());
            }
            stringFromStand = new HPVineBarrierEntity(level, this, this.getUserPower());
            stringFromStand.setOriginBlockPos(blockPos);
            level.addFreshEntity(stringFromStand);
            playSound(InitSounds.HP_GRAPPLE_CATCH.get(), 1.0F, 1.0F);
        }
    }

    public int getPlacedBarriersCount() {
        return entityData.get(PLACED_BARRIERS);
    }

    private void setPlacedBarriersCount(int value) {
        entityData.set(PLACED_BARRIERS, value);
    }


    @Override
    protected void defineSynchedData(){
        super.defineSynchedData();
        entityData.define(MODE,0);
        entityData.define(TARGET,"random");
        entityData.define(PLACED_BARRIERS, 0);
    }
    private final StandRelativeOffset offsetDefault = StandRelativeOffset.withYOffset(0, 0, 0);

    @Override
    public boolean isPickable(){ return false;}

	public StandRelativeOffset getDefaultOffsetFromUser() {return offsetDefault;}


    public void setMode(int mode){
        entityData.set(MODE,mode);
    }

    public int getMode(){
        return entityData.get(MODE);
    }

    public void setTarget(String target){
        entityData.set(TARGET,target);
    }

    public String getTarget(){
        return entityData.get(TARGET);
    }


    @Override
    public float getLeapStrength() {
        AtomicReference<Float> strength = new AtomicReference<>((float) 0);
        if(this.getUser() != null){
            INonStandPower.getNonStandPowerOptional(this.getUser()).ifPresent(ipower->{
                Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                if(hamonOp.isPresent()){
                    HamonData hamon = hamonOp.get();
                    strength.set(hamon.isSkillLearned(ModHamonSkills.AFTERIMAGES.get()) ? 2F : 1.25F);
                    ModifiableAttributeInstance speedAttribute = this.getUser().getAttribute(Attributes.MOVEMENT_SPEED);
                    if (speedAttribute != null) {
                        strength.updateAndGet(v -> new Float((v * (float) (speedAttribute.getValue() / speedAttribute.getBaseValue()))));
                    }
                }
            });
        }
        return strength.get();
    }

}
