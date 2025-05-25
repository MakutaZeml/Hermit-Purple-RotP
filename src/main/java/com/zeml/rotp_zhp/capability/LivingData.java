package com.zeml.rotp_zhp.capability;

import com.zeml.rotp_zhp.network.ModNetwork;
import com.zeml.rotp_zhp.network.packets.CanLeapPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LivingData implements INBTSerializable<CompoundNBT> {
    private final LivingEntity entity;
    private boolean triedHermit = false;
    private boolean canLeap = false;
    private boolean isBreathing = false;
    private int mode = 0;


    public LivingData(LivingEntity entity) {
        this.entity = entity;
    }

    public void setTriedHermit(boolean triedHermit) {
        this.triedHermit = triedHermit;
    }

    public boolean isTriedHermit() {
        return this.triedHermit;
    }

    public void setCanLeap(boolean canLeap) {
        this.canLeap = canLeap;
        if(entity instanceof ServerPlayerEntity){
            ModNetwork.sendToClient(new CanLeapPacket(entity.getId(),canLeap), (ServerPlayerEntity) entity);
        }
    }

    public boolean isCanLeap() {
        return canLeap;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }


    public void syncWithAnyPlayer(ServerPlayerEntity player) {

        //AddonPackets.sendToClient(new TrPickaxesThrownPacket(entity.getId(), pickaxesThrown), player);
    }

    // If there is data that should only be known to the player, and not to other ones, sync it here instead.
    public void syncWithEntityOnly(ServerPlayerEntity player) {
        ModNetwork.sendToClient(new CanLeapPacket(player.getId(),this.canLeap), player);
    }



    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("TriedHermitPurple",this.triedHermit);
        nbt.putBoolean("canPurpleLeap",this.canLeap);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.triedHermit = nbt.getBoolean("TriedHermitPurple");
        this.canLeap = nbt.getBoolean("canPurpleLeap");
    }
}
