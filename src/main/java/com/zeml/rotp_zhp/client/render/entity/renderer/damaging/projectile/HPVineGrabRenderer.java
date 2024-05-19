package com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile;

import com.zeml.rotp_zhp.client.render.entity.model.projectile.HPVineModel;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineGrabEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HPVineGrabRenderer extends HPVineAbstractRenderer<HPVineGrabEntity> {

    public HPVineGrabRenderer(EntityRendererManager renderManager) {
        super(renderManager, new HPVineModel<HPVineGrabEntity>());
    }
}
