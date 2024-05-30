package com.zeml.rotp_zhp.entity.stand.stands;


import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandRelativeOffset;
import com.github.standobyte.jojo.entity.stand.StandEntityType;

import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import java.util.Optional;

public class HermitPurpleEntity extends StandEntity {

    private static final DataParameter<Integer> MODE = EntityDataManager.defineId(HermitPurpleEntity.class,DataSerializers.INT);
    private static final DataParameter<String> TARGET = EntityDataManager.defineId(HermitPurpleEntity.class,DataSerializers.STRING);


    public HermitPurpleEntity(StandEntityType<HermitPurpleEntity> type, World world){
        super(type, world);
        unsummonOffset = getDefaultOffsetFromUser().copy();
    }

    @Override
    protected void defineSynchedData(){
        super.defineSynchedData();
        entityData.define(MODE,0);
        entityData.define(TARGET,"random");
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

}
