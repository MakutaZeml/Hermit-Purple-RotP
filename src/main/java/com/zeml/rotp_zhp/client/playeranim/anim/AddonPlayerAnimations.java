package com.zeml.rotp_zhp.client.playeranim.anim;

import com.github.standobyte.jojo.client.playeranim.PlayerAnimationHandler;
import com.github.standobyte.jojo.client.playeranim.anim.interfaces.WindupAttackAnim;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.util.ResourceLocation;


public class AddonPlayerAnimations {
    public static WindupAttackAnim grabCommand;
    public static WindupAttackAnim grab;
    public static WindupAttackAnim doxx;
    public static WindupAttackAnim vine;
    public static WindupAttackAnim summon_emp;


    public static void init(){
        grabCommand = PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmxGrabCommandHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "grapple"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);

        grab = PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmxGrabHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "grab"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);

        doxx =PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmxDoxxLeftHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "doxx"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);
        vine = PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmxVineHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "vine"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);
        summon_emp = PlayerAnimationHandler.getPlayerAnimator().registerAnimLayer(
                "com.zeml.rotp_zhp.client.playeranim.anim.kosmimpl.KosmXSummonEmperorHandler",
                new ResourceLocation(RotpHermitPurpleAddon.MOD_ID, "summon_emp"), 1,
                WindupAttackAnim.NoPlayerAnimator::new);


    }
}
