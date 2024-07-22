package com.zeml.rotp_zhp.network;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.network.packets.fromserver.CommonConfigPacket;
import com.zeml.rotp_zhp.HermitConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PurplCommonConfigPacket {
    private final HermitConfig.Common.SyncedValues values;

    public PurplCommonConfigPacket(HermitConfig.Common.SyncedValues values){
        this.values= values;
    }

    public static class Handler implements IModPacketHandler<PurplCommonConfigPacket> {

        @Override
        public void encode(PurplCommonConfigPacket msg, PacketBuffer buf) {
            msg.values.writeToBuf(buf);
        }

        @Override
        public PurplCommonConfigPacket decode(PacketBuffer buf) {
            return new PurplCommonConfigPacket(new HermitConfig.Common.SyncedValues(buf));
        }

        @Override
        public void handle(PurplCommonConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
            msg.values.changeConfigValues();
        }

        @Override
        public Class<PurplCommonConfigPacket> getPacketClass() {
            return PurplCommonConfigPacket.class;
        }
    }
}
