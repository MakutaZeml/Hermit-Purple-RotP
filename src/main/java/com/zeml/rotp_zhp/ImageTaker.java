package com.zeml.rotp_zhp;

import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import com.zeml.rotp_zhp.action.stand.HPDoxx;
import com.zeml.rotp_zhp.client.ClientReflection;
import de.maxhenkel.camera.ImageProcessor;
import de.maxhenkel.camera.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.hooks.BasicEventHooks;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class ImageTaker {
    private LivingEntity target;
    private static boolean takeScreenshot;
    private static UUID uuid;
    private static boolean hide;

    public static void takeScreenshot(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        posToTakePicture = new BlockPos(228, 69, 420);

        hide = mc.options.hideGui;
        mc.options.hideGui = true;


//        takeScreenshot = true;
        uuid = id;
        mc.setScreen(null);

        initRemoteBuffer(mc);
        Framebuffer mainBuffer = mc.getMainRenderTarget();
        ClientReflection.setMainRenderTarget(mc, remoteRenderTarget);
        renderOnRemoteBuffer(Minecraft.getInstance());
        NativeImage image = takeScreenshot(mc.getWindow().getWidth(), mc.getWindow().getHeight(), remoteRenderTarget);
        ImageProcessor.sendScreenshodThreaded(uuid, image);
        ClientReflection.setMainRenderTarget(mc, mainBuffer);

        mc.options.hideGui = hide;
//        takeScreenshot = false;
    }

    @SubscribeEvent
    public static void onRenderTickEnd(TickEvent.RenderTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.END)) {
            return;
        }

        if (!takeScreenshot) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        renderOnRemoteBuffer(Minecraft.getInstance());
        NativeImage image = takeScreenshot(mc.getWindow().getWidth(), mc.getWindow().getHeight(), mc.getMainRenderTarget());

        mc.options.hideGui = hide;
        takeScreenshot = false;

        ImageProcessor.sendScreenshodThreaded(uuid, image);
    }

    public static NativeImage takeScreenshot(int width, int height, Framebuffer buffer) {
        width = buffer.width;
        height = buffer.height;
        NativeImage nativeimage = new NativeImage(width, height, false);
        RenderSystem.bindTexture(buffer.getColorTextureId());
        nativeimage.downloadTexture(0, true);
        nativeimage.flipY();
        return nativeimage;
    }

    private static BlockPos posToTakePicture;

    @SubscribeEvent
    public static void pictureCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (Minecraft.getInstance().getMainRenderTarget() == remoteRenderTarget) {
            ActiveRenderInfo camera = event.getInfo();
            Minecraft mc = Minecraft.getInstance();
            Entity player = mc.player;

            LivingEntity target = HPDoxx.hpObj((PlayerEntity) player);

            posToTakePicture = new BlockPos(target.getX()+ Math.cos(target.yRot),target.getY()+1,target.getZ()+Math.sin(target.yRot));

            if (posToTakePicture != null) {
                ClientReflection.setPosition(camera, Vector3d.atCenterOf(posToTakePicture));
            }
            event.setRoll(30);
            event.setYaw(target.yRot);
            event.setPitch(60);
            ClientReflection.setIsDetached(camera, true);
            ClientReflection.setMirror(camera, false);
        }
    }



    static Framebuffer remoteRenderTarget;
    private static void initRemoteBuffer(Minecraft mc) {
        if (remoteRenderTarget == null) {
            remoteRenderTarget = new Framebuffer(mc.getWindow().getWidth(), mc.getWindow().getHeight(), true, Minecraft.ON_OSX) {
                @Override
                public void blitToScreen(int width, int height, boolean flag) {}
            };
            remoteRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    private static void renderOnRemoteBuffer(Minecraft mc) {
        RenderSystem.pushMatrix();
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        remoteRenderTarget.bindWrite(true);
        FogRenderer.setupNoFog();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        if (!mc.noRender) {
            float partialTick = mc.isPaused() ? ClientReflection.getPausePartialTick(mc) : mc.getFrameTime();
            BasicEventHooks.onRenderTickStart(partialTick);

            RenderSystem.viewport(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
            if (mc.level != null) {
                mc.getProfiler().push("level");
                mc.gameRenderer.renderLevel(partialTick, Util.getNanos(), new MatrixStack());
                mc.levelRenderer.doEntityOutline();

                remoteRenderTarget.bindWrite(true);
            }

            BasicEventHooks.onRenderTickEnd(partialTick);
        }

        remoteRenderTarget.unbindWrite();
        RenderSystem.popMatrix();
    }


}