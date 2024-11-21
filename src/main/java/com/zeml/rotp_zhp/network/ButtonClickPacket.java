package com.zeml.rotp_zhp.network;

import com.github.standobyte.jojo.network.NetworkUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.network.packets.fromclient.ClSetStandSkinPacket;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EntityPredicates;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ButtonClickPacket {
    private int mode;
    private String target;

    public ButtonClickPacket(int mode, String target) {
        this.mode = mode;
        this.target = target;
        System.out.println("Paquete creado: mode=" + mode + ", target=" + target);
    }


    public static class Handler implements IModPacketHandler<ButtonClickPacket>{

        @Override
        public void encode(ButtonClickPacket packet, PacketBuffer buf) {
            buf.writeInt(packet.mode);
            buf.writeUtf(packet.target);
        }

        @Override
        public ButtonClickPacket decode(PacketBuffer buf) {
            int mode = buf.readInt();
            String target = buf.readUtf(32767);
            return new ButtonClickPacket(mode, target);
        }

        @Override
        public void handle(ButtonClickPacket buttonClickPacket, Supplier<NetworkEvent.Context> ctx) {

            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();
                if (player != null) {
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) IStandPower.getPlayerStandPower(player).getStandManifestation();
                    if (hermitPurple != null) {
                        hermitPurple.setMode(buttonClickPacket.mode);
                        hermitPurple.setTarget(buttonClickPacket.target);
                        System.out.print(hermitPurple.getUser().getName().getString());
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
        public Class<ButtonClickPacket> getPacketClass() {
            return ButtonClickPacket.class;
        }
    }

}