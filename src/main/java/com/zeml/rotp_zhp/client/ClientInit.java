package com.zeml.rotp_zhp.client;

import com.github.standobyte.jojo.client.render.entity.layerrenderer.HamonBurnLayer;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.render.entity.model.stand.HermitoUserModel;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoThorns;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoUserLayer;
import com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile.*;
import com.zeml.rotp_zhp.client.render.entity.renderer.stand.HermitPurpleRenderer;
import com.zeml.rotp_zhp.init.AddonStands;

import com.zeml.rotp_zhp.init.InitEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = event.getMinecraftSupplier().get();;

        RenderingRegistry.registerEntityRenderingHandler(AddonStands.HERMITOPURLE.getEntityType(), HermitPurpleRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_GRAPPLING_VINE.get(), HPGrapplingVineRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_VINE.get(), HPVineRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_GRAB_ENTITY.get(), HPVineGrabRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_BARRIER.get(), HPVineBarrierRrnderer::new);


        event.enqueueWork(() -> {
            Map<String, PlayerRenderer> skinMap = mc.getEntityRenderDispatcher().getSkinMap();
            addLayers(skinMap.get("default"), false);
            addLayers(skinMap.get("slim"), true);
            mc.getEntityRenderDispatcher().renderers.values().forEach(ClientInit::addLayersToEntities);
        });


    }


    private static void addLayers(PlayerRenderer renderer, boolean slim){
        renderer.addLayer(new HermitoUserLayer<>(renderer,new HermitoUserModel<>(0.35F,slim),slim));
        renderer.addLayer(new HermitoThorns<>(renderer,new HermitoUserModel<>(0.35F,slim),slim));

        addLivingLayers(renderer);
        addBipedLayers(renderer);
    }



    private static <T extends LivingEntity, M extends BipedModel<T>> void addLayersToEntities(EntityRenderer<?> renderer) {
        if (renderer instanceof LivingRenderer<?, ?>) {
            addLivingLayers((LivingRenderer<T, ?>) renderer);
            if (((LivingRenderer<?, ?>) renderer).getModel() instanceof BipedModel<?>) {
                addBipedLayers((LivingRenderer<T, M>) renderer);
            }
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addLivingLayers(@NotNull LivingRenderer<T, M> renderer) {

        renderer.addLayer(new HamonBurnLayer<>(renderer));
    }

    private static <T extends LivingEntity, M extends BipedModel<T>> void addBipedLayers(LivingRenderer<T, M> renderer) {
    }


    }
