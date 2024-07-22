package com.zeml.rotp_zhp.network;

import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.zeml.rotp_zhp.HermitConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ResetConfigPacket {

    public ResetConfigPacket(){}

    public static class Handler implements IModPacketHandler<ResetConfigPacket> {

        @Override
        public void encode(ResetConfigPacket msg, PacketBuffer buf) {
        }

        @Override
        public ResetConfigPacket decode(PacketBuffer buf) {
            return new ResetConfigPacket();
        }

        @Override
        public void handle(ResetConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
            HermitConfig.Common.SyncedValues.resetConfig();
        }

        @Override
        public Class<ResetConfigPacket> getPacketClass() {
            return ResetConfigPacket.class;
        }
    }
}
