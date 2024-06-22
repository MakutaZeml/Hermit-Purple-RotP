package com.zeml.rotp_zhp.network;

import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel clientChannel;
    private static int packetIndex = 0;

    public static void init(){

        clientChannel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "client_channel"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();

        packetIndex = 0;
        registerMessage(clientChannel,new ButtonClickPacket.Handler(),Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }


    public static void sendToServer(Object msg) {
        clientChannel.sendToServer(msg);
    }
    private static <MSG> void registerMessage(SimpleChannel channel, IModPacketHandler<MSG> handler, Optional<NetworkDirection> networkDirection) {
        if (packetIndex > 127) {
            throw new IllegalStateException("Too many packets (> 127) registered for a single channel!");
        }
        channel.registerMessage(packetIndex++, handler.getPacketClass(), handler::encode, handler::decode, handler::enqueueHandleSetHandled, networkDirection);
    }

}
