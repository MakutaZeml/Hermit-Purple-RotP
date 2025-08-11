package com.zeml.rotp_zhp.client.render.entity.renderer.stand;

import com.github.standobyte.jojo.client.render.entity.renderer.stand.StandEntityRenderer;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.render.entity.model.stand.EmperorModel;
import com.zeml.rotp_zhp.entity.stand.stands.EmperorEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class EmperorRenderer extends StandEntityRenderer<EmperorEntity, EmperorModel> {

    public EmperorRenderer(EntityRendererManager renderManager) {
        super(renderManager, new EmperorModel(), new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "textures/entity/stand/void.png"), 0);
    }

}
