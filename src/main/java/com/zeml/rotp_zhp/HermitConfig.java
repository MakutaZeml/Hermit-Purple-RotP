package com.zeml.rotp_zhp;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.CommonConfigPacket;
import com.github.standobyte.jojo.network.packets.fromserver.ResetSyncedCommonConfigPacket;
import com.zeml.rotp_zhp.network.PurplCommonConfigPacket;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class HermitConfig {

    public static class Common{
        private boolean loaded = false;
        public final ForgeConfigSpec.BooleanValue hamonToHermit;
        public final ForgeConfigSpec.IntValue control;
        public final ForgeConfigSpec.IntValue strength;
        public final ForgeConfigSpec.IntValue breathing;

        private Common(ForgeConfigSpec.Builder builder) {
            this(builder, null);
        }

        private Common(ForgeConfigSpec.Builder builder, @Nullable String mainPath){
            if (mainPath != null) {
                builder.push(mainPath);
            }
            builder.push("Can Hamon give Hermit Purple");
            hamonToHermit = builder.translation("rotp_zhp.config.hamonToHermit")
                    .define("hamonToHermit",false);
            control = builder.comment("Hamon control level to obtain Hermit Purple")
                    .translation("rotp_zhp.config.control")
                    .defineInRange("control",60,0,60);
            strength = builder.comment("Hamon strength level to obtain Hermit Purple")
                    .translation("rotp_zhp.config.strength")
                    .defineInRange("strength",60,0,60);
            breathing = builder.comment("Hamon breathing level to obtain Hermit Purple")
                    .translation("rotp_zhp.config.breathing")
                    .defineInRange("breathing",100,0,100);
            builder.pop();
        }

        public boolean isConfigLoaded() {
            return loaded;
        }

        private void onLoadOrReload() {
            loaded = true;
        }

        public static class SyncedValues {
            private final boolean hamonToHermit;
            private final int control;
            private final int strength;
            private final int breathing;

            public SyncedValues(PacketBuffer buf){
                hamonToHermit = buf.readBoolean();
                control = buf.readInt();
                strength = buf.readInt();
                breathing = buf.readInt();

            }
            public void writeToBuf(PacketBuffer buf){
                buf.writeBoolean(hamonToHermit);
                buf.writeInt(control);
                buf.writeInt(strength);
                buf.writeInt(breathing);
            }
            private SyncedValues(Common config){
                hamonToHermit = config.hamonToHermit.get();
                control = config.control.get();
                strength = config.strength.get();
                breathing = config.breathing.get();
            }

            public void changeConfigValues(){
                COMMON_SYNCED_TO_CLIENT.hamonToHermit.set(hamonToHermit);
                COMMON_SYNCED_TO_CLIENT.control.set(control);
                COMMON_SYNCED_TO_CLIENT.strength.set(strength);
                COMMON_SYNCED_TO_CLIENT.breathing.set(breathing);
            }
            public static void resetConfig() {
                COMMON_SYNCED_TO_CLIENT.hamonToHermit.clearCache();
                COMMON_SYNCED_TO_CLIENT.control.clearCache();
                COMMON_SYNCED_TO_CLIENT.strength.clearCache();
                COMMON_SYNCED_TO_CLIENT.breathing.clearCache();
            }
            public static void syncWithClient(ServerPlayerEntity player) {
                PacketManager.sendToClient(new PurplCommonConfigPacket(new Common.SyncedValues(COMMON_FROM_FILE)), player);
            }
            public static void onPlayerLogout(ServerPlayerEntity player) {
                PacketManager.sendToClient(new ResetSyncedCommonConfigPacket(), player);
            }
        }
    }
    static final ForgeConfigSpec commonSpec;
    private static final Common COMMON_FROM_FILE;
    private static final Common COMMON_SYNCED_TO_CLIENT;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON_FROM_FILE = specPair.getLeft();

        // how tf do the configs work?
        final Pair<Common, ForgeConfigSpec> syncedSpecPair = new ForgeConfigSpec.Builder().configure(builder -> new Common(builder, "synced"));
        CommentedConfig config = CommentedConfig.of(InMemoryCommentedFormat.defaultInstance());
        ForgeConfigSpec syncedSpec = syncedSpecPair.getRight();
        syncedSpec.correct(config);
        syncedSpec.setConfig(config);
        COMMON_SYNCED_TO_CLIENT = syncedSpecPair.getLeft();
    }
    public static Common getCommonConfigInstance(boolean isClientSide) {
        return isClientSide && !ClientUtil.isLocalServer() ? COMMON_SYNCED_TO_CLIENT : COMMON_FROM_FILE;
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (RotpHermitPurpleAddon.MOD_ID.equals(config.getModId()) && config.getType() == ModConfig.Type.COMMON) {
            COMMON_FROM_FILE.onLoadOrReload();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfig.Reloading event) {
        ModConfig config = event.getConfig();
        if (RotpHermitPurpleAddon.MOD_ID.equals(config.getModId()) && config.getType() == ModConfig.Type.COMMON) {
            // FIXME sync the config to all players on the server
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.getPlayerList().getPlayers().forEach(player -> {
                    Common.SyncedValues.syncWithClient(player);
                });
            }
        }
    }

}
