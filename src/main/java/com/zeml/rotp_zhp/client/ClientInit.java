package com.zeml.rotp_zhp.client;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.playeranim.anim.ModPlayerAnimations;
import com.github.standobyte.jojo.client.render.armor.ArmorModelRegistry;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.HamonBurnLayer;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.playeranim.anim.AddonPlayerAnimations;
import com.zeml.rotp_zhp.client.render.entity.model.stand.HermitoUserModel;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoThorns;
import com.zeml.rotp_zhp.client.render.entity.renderer.HermitoUserLayer;
import com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile.*;
import com.zeml.rotp_zhp.client.render.entity.renderer.stand.EmperorRenderer;
import com.zeml.rotp_zhp.client.render.entity.renderer.stand.HermitPurpleRenderer;
import com.zeml.rotp_zhp.init.AddonStands;

import com.zeml.rotp_zhp.init.InitEntities;
import com.zeml.rotp_zhp.init.InitItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {

    private static final IItemPropertyGetter STAND_ITEM_INVISIBLE = (itemStack, clientWorld, livingEntity) -> {
        return !ClientUtil.canSeeStands() ? 1 : 0;
    };

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = event.getMinecraftSupplier().get();;

        RenderingRegistry.registerEntityRenderingHandler(AddonStands.HERMITOPURLE.getEntityType(), HermitPurpleRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_GRAPPLING_VINE.get(), HPGrapplingVineRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_VINE.get(), HPVineRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_GRAB_ENTITY.get(), HPVineGrabRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.HP_BARRIER.get(), HPVineBarrierRrnderer::new);

        RenderingRegistry.registerEntityRenderingHandler(AddonStands.EMPEROR_STAND.getEntityType(), EmperorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.EMPEROR_BULLET.get(), EmperorBulletRenderer::new);


        event.enqueueWork(() -> {
            Map<String, PlayerRenderer> skinMap = mc.getEntityRenderDispatcher().getSkinMap();
            addLayers(skinMap.get("default"), false);
            addLayers(skinMap.get("slim"), true);
            mc.getEntityRenderDispatcher().renderers.values().forEach(ClientInit::addLayersToEntities);
            ItemModelsProperties.register(InitItems.EMPEROR.get(),
                    new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "stand_invisible"),
                    STAND_ITEM_INVISIBLE);
        });


    }


    private static void addLayers(PlayerRenderer renderer, boolean slim){
        renderer.addLayer(new HermitoUserLayer<>(renderer,new HermitoUserModel<>(0.35F,slim),slim));
        renderer.addLayer(new HermitoThorns<>(renderer,new HermitoUserModel<>(0.35F,slim),slim));

        addLivingLayers(renderer);
        addBipedLayers(renderer);
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void loadCustomArmorModels(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ArmorModelRegistry.loadArmorModels();

            AddonPlayerAnimations.init();
        });
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
