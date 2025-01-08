package com.zeml.rotp_zhp.client.render.entity.renderer.damaging.projectile;

import com.github.standobyte.jojo.client.render.entity.renderer.damaging.extending.ExtendingEntityRenderer;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.entity.damaging.projectile.HGEmeraldEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.client.render.entity.model.projectile.HPVineModel;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public abstract class HPVineAbstractRenderer<T extends OwnerBoundProjectileEntity> extends ExtendingEntityRenderer<T, HPVineModel<T>> {
    private static final ResourceLocation VINE = new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "textures/entity/projectiles/hp_vine.png");
    private static ResourceLocation VAINA = VINE;

    public HPVineAbstractRenderer(EntityRendererManager renderManager, HPVineModel<T> model) {
        super(renderManager, model, VAINA);
    }

    private final StandType<?> HP = InitStands.STAND_HERMITO_PURPLE.getStandType();

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if(entity.getOwner() != null){
            LivingEntity owner = entity.getOwner();
            if(owner != null){
                if(owner instanceof StandEntity) owner = ((StandEntity) owner).getUser();
                if(owner != null){
                    IStandPower.getStandPowerOptional(owner).ifPresent(power -> {
                        if(power.getType() == HP){
                            VAINA =StandSkinsManager.getInstance() != null? (StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                                    .getStandSkin(power.getStandInstance().get()), VINE)): VINE;

                        }else VAINA = VINE;
                    });
                }else VAINA = VINE;
            }else VAINA = VINE;

        }
        return VAINA;
    }
}
