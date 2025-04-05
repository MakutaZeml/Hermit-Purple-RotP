package com.zeml.rotp_zhp.client.playeranim.anim;

import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.client.playeranim.PlayerAnimationHandler;
import com.github.standobyte.jojo.client.playeranim.anim.interfaces.BasicToggleAnim;
import com.github.standobyte.jojo.client.playeranim.anim.interfaces.WindupAttackAnim;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.util.ResourceLocation;


public class AddonPlayerAnimations {
    public static WindupAttackAnim grabCommand;
    public static WindupAttackAnim grab;
    public static BasicToggleAnim vine;

    public static void init(){
        grabCommand = PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmGrabCommandHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "grapple"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);

        grab = PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmxGrabHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "grab"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);
        vine= PlayerAnimationHandler.getPlayerAnimator().registerBasicAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmxVineAttackHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "grab"), 1);
    }
}
