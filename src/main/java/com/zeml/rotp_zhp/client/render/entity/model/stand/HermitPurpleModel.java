package com.zeml.rotp_zhp.client.render.entity.model.stand;

import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.github.standobyte.jojo.client.render.entity.model.stand.HumanoidStandModel;
import com.github.standobyte.jojo.client.render.entity.pose.ModelPose;
import com.github.standobyte.jojo.client.render.entity.pose.RotationAngle;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class HermitPurpleModel  extends HumanoidStandModel<HermitPurpleEntity> {


	public HermitPurpleModel() {
		super();

		addHumanoidBaseBoxes(null);
		texWidth = 128;
		texHeight = 128;

		leftArm.texOffs(71, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.2F, false);
		leftForeArm.texOffs(71, 10).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.2F, false);

		rightArm.texOffs(39, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.2F, false);
		rightForeArm.texOffs(39, 10).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.2F, false);

		head.texOffs(16, 0).addBox(7.0F, -8.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.0F, false);
		head.texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.2F, false);

	}


	@Override
	protected ModelPose initIdlePose() {
		return new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(upperPart, 0, 0, 0),
				RotationAngle.fromDegrees(leftArm,-89.572861776f, 54.3059464084F, -1.940648053F),
				RotationAngle.fromDegrees(rightArm, -174.9760953588F, -75.0286896791F, 140.0334466262F)

		});
	}


}