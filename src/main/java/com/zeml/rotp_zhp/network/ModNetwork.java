package com.zeml.rotp_zhp.network;

import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.network.packets.CanLeapPacket;
import com.zeml.rotp_zhp.network.packets.ColorPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel serverChannel;

    private static SimpleChannel clientChannel;
    private static int packetIndex = 0;

    public static void init(){
        serverChannel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "server_channel"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();
        clientChannel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "client_channel"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();

        packetIndex = 0;
        registerMessage(clientChannel,new ButtonClickPacket.Handler(),Optional.of(NetworkDirection.PLAY_TO_SERVER));
        registerMessage(clientChannel,new ColorPacket.Handler(),Optional.of(NetworkDirection.PLAY_TO_SERVER));

        registerMessage(serverChannel, new PurplCommonConfigPacket.Handler(),Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        registerMessage(serverChannel,new ResetConfigPacket.Handler(),Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        serverChannel.registerMessage(packetIndex++, CanLeapPacket.class,
                CanLeapPacket::encode,CanLeapPacket::decode,CanLeapPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }


    public static void sendToServer(Object msg) {
        clientChannel.sendToServer(msg);
    }

    public static void sendToClient(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            serverChannel.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }
    private static <MSG> void registerMessage(SimpleChannel channel, IModPacketHandler<MSG> handler, Optional<NetworkDirection> networkDirection) {
        if (packetIndex > 127) {
            throw new IllegalStateException("Too many packets (> 127) registered for a single channel!");
        }
        channel.registerMessage(packetIndex++, handler.getPacketClass(), handler::encode, handler::decode, handler::enqueueHandleSetHandled, networkDirection);
    }

}
