package com.zeml.rotp_zhp.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LivingData implements INBTSerializable<CompoundNBT> {
    private final LivingEntity entity;
    private boolean triedHermit = false;

    public LivingData(LivingEntity entity) {
        this.entity = entity;
    }

    public void setTriedHermit(boolean triedHermit) {
        this.triedHermit = triedHermit;
    }

    public boolean isTriedHermit() {
        return this.triedHermit;
    }

    public void syncWithAnyPlayer(ServerPlayerEntity player) {

        //AddonPackets.sendToClient(new TrPickaxesThrownPacket(entity.getId(), pickaxesThrown), player);
    }

    // If there is data that should only be known to the player, and not to other ones, sync it here instead.
    public void syncWithEntityOnly(ServerPlayerEntity player) {

//        AddonPackets.sendToClient(new SomeDataPacket(someDataField), player);
    }




    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("TriedHermitPurple",this.triedHermit);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.triedHermit = nbt.getBoolean("TriedHermitPurple");
    }
}
