package com.zeml.rotp_zhp.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.zeml.rotp_zhp.util.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ClientReflection {

    private static final Field MINECRAFT_PAUSE_PARTIAL_TICK = ObfuscationReflectionHelper.findField(Minecraft.class, "field_193996_ah");
    public static float getPausePartialTick(Minecraft mc) {
        return ReflectionUtil.getFloatFieldValue(MINECRAFT_PAUSE_PARTIAL_TICK, mc);
    }

    private static final Field MINECRAFT_MAIN_RENDER_TARGET = ObfuscationReflectionHelper.findField(Minecraft.class, "field_147124_at");
    public static void setMainRenderTarget(Minecraft mc, Framebuffer buffer) {
        ReflectionUtil.setFieldValue(MINECRAFT_MAIN_RENDER_TARGET, mc, buffer);
    }

    private static final Method ACTIVE_RENDER_INFO_SET_POSITION = ObfuscationReflectionHelper.findMethod(ActiveRenderInfo.class, "func_216774_a", Vector3d.class);
    public static void setPosition(ActiveRenderInfo camera, Vector3d position) {
        ReflectionUtil.invokeMethod(ACTIVE_RENDER_INFO_SET_POSITION, camera, position);
    }

    private static final Field ACTIVE_RENDER_INFO_DETACHED = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216799_k");
    public static void setIsDetached(ActiveRenderInfo camera, boolean detached) {
        ReflectionUtil.setBooleanFieldValue(ACTIVE_RENDER_INFO_DETACHED, camera, detached);
    }

    private static final Field ACTIVE_RENDER_INFO_MIRROR = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216800_l");
    public static void setMirror(ActiveRenderInfo camera, boolean mirror) {
        ReflectionUtil.setBooleanFieldValue(ACTIVE_RENDER_INFO_MIRROR, camera, mirror);
    }
}
