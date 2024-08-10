package com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile;

import com.github.standobyte.jojo.client.render.entity.renderer.damaging.extending.ExtendingEntityRenderer;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.entity.damaging.projectile.HGEmeraldEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.render.entity.model.projectile.HPVineModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public abstract class HPVineAbstractRenderer<T extends OwnerBoundProjectileEntity> extends ExtendingEntityRenderer<T, HPVineModel<T>> {
    private static final ResourceLocation VINE = new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "textures/entity/projectiles/hp_vine.png");

    public HPVineAbstractRenderer(EntityRendererManager renderManager, HPVineModel<T> model) {
        super(renderManager, model, VINE);
    }


}
