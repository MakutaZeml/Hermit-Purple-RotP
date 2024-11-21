package com.zeml.rotp_zhp.network.packets;

import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EntityPredicates;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ColorPacket {
    private int color;


    public ColorPacket(int mode) {
        this.color = mode;
    }


    public static class Handler implements IModPacketHandler<ColorPacket> {

        @Override
        public void encode(ColorPacket packet, PacketBuffer buf) {
            buf.writeInt(packet.color);
        }

        @Override
        public ColorPacket decode(PacketBuffer buf) {
            int mode = buf.readInt();
            return new ColorPacket(mode);
        }

        @Override
        public void handle(ColorPacket ColorPacket, Supplier<NetworkEvent.Context> ctx) {

            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();
                if (player != null) {
                    HermitPurpleEntity hermitPurple =(HermitPurpleEntity) IStandPower.getPlayerStandPower(player).getStandManifestation();
                    if (hermitPurple != null) {
                        hermitPurple.setColor(ColorPacket.color);
                        System.out.println(ColorPacket.color);
                    } else {
                        System.out.println("HermitPurpleEntity no encontrado");
                    }
                } else {
                    System.out.println("Player es null");
                }
            });
            context.setPacketHandled(true);
        }

        @Override
        public Class<ColorPacket> getPacketClass() {
            return ColorPacket.class;
        }
    }
}
