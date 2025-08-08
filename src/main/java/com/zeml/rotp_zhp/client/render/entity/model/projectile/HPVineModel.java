package com.zeml.rotp_zhp.client.render.entity.model.projectile;

import com.github.standobyte.jojo.client.render.entity.model.ownerbound.repeating.RepeatingModel;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HPVineModel<T extends OwnerBoundProjectileEntity> extends RepeatingModel<T> {
    private final ModelRenderer vine;
    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;
    private final ModelRenderer cube_r3;
    private final ModelRenderer cube_r4;
    private final ModelRenderer cube_r5;
    private final ModelRenderer cube_r6;

    public HPVineModel() {
        texWidth = 64;
        texHeight = 64;

        vine = new ModelRenderer(this);
        vine.setPos(0.0F, 0F, 0.0F);
        vine.texOffs(10, 26).addBox(0.0F, -1.0F, -4.0F, 1.0F, 1.0F, 8.0F, 0.0F, false);

        cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(2.0F, 0.0F, 3.0F);
        vine.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, -1.0472F);
        cube_r1.texOffs(37, 5).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_r1.texOffs(37, 9).addBox(-1.0F, -2.0F, -6.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(2.0F, 0.0F, 1.0F);
        vine.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, -1.3526F);
        cube_r2.texOffs(37, 9).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(2.0F, -1.0F, 4.0F);
        vine.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, -1.3526F);
        cube_r3.texOffs(31, 5).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(1.0F, 1.0F, -1.0F);
        vine.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, -0.2618F);
        cube_r4.texOffs(31, 5).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(2.0F, 1.0F, 2.0F);
        vine.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.0F, 0.0F, -0.2618F);
        cube_r5.texOffs(31, 5).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(1.0F, 0.0F, 0.0F);
        vine.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.0F, 0.0F, -0.6545F);
        cube_r6.texOffs(37, 5).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
    }

    @Override
    protected ModelRenderer getMainPart() {
        return null;
    }

    @Override
    protected float getMainPartLength() {
        return 0;
    }

    @Override
    protected ModelRenderer getRepeatingPart() {
        return vine;
    }

    @Override
    protected float getRepeatingPartLength() {
        return 8F;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}