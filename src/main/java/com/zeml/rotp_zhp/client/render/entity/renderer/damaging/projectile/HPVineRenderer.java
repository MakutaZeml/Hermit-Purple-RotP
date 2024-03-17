package com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile;

import com.zeml.rotp_zhp.client.render.entity.model.projectile.HPVineModel;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HPVineRenderer extends HPVineAbstractRenderer<HPVineEntity> {

    public HPVineRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HPVineModel<HPVineEntity>());
    }
}
