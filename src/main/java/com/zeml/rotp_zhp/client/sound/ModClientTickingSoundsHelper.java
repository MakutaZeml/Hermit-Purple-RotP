package com.zeml.rotp_zhp.client.sound;


import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.sound.StoppableEntityTickableSound;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = RotpHermitPurpleAddon.MOD_ID, value = Dist.CLIENT)
public abstract class ModClientTickingSoundsHelper {


    public static void playStandEntityCancelableActionSound(StandEntity stand, SoundEvent sound,
                                                            StandEntityAction action, @Nullable StandEntityAction.Phase phase, float volume, float pitch, boolean looping) {
        Minecraft mc = Minecraft.getInstance();


        SoundCategory category = stand.getSoundSource();
        PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(stand, sound, category, volume, pitch);
        if (event.isCanceled() || event.getSound() == null) return;
        sound = event.getSound();
        category = event.getCategory();
        volume = event.getVolume();
        pitch = event.getPitch();

        mc.getSoundManager().play(new StoppableEntityTickableSound<StandEntity>(sound, category, volume, pitch, looping, stand, e ->
                e.getCurrentTaskAction() == action && (phase == null || e.getCurrentTaskPhase().map(stPhase -> stPhase == phase).orElse(false))));
    }
}
