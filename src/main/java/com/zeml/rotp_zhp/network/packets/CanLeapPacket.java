package com.zeml.rotp_zhp.network.packets;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.capability.LivingData;
import com.zeml.rotp_zhp.capability.LivingDataProvider;
import com.zeml.rotp_zhp.power.impl.stand.type.HermitPurpleStandType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CanLeapPacket {
    private final int entityID;
    private final boolean canLeap;

    public CanLeapPacket(int entityID, boolean stay){
        this.entityID = entityID;
        this.canLeap = stay;
    }


    public static void encode(CanLeapPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeBoolean(msg.canLeap);
    }

    public static CanLeapPacket decode(PacketBuffer buf) {
        return new CanLeapPacket(buf.readInt(), buf.readBoolean());
    }


    public static void handle(CanLeapPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = ClientUtil.getEntityById(msg.entityID);
            if (entity instanceof LivingEntity) {
                IStandPower.getStandPowerOptional((LivingEntity) entity).ifPresent(power -> {
                    if(entity.isAlive()){
                        ((HermitPurpleStandType<?>) power.getType()).setLeapUnlocked(msg.canLeap);
                    }

                });
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
