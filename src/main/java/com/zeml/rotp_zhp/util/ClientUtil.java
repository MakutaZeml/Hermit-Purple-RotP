package com.zeml.rotp_zhp.util;

import com.zeml.rotp_zhp.client.ui.screen.HPScreenTargetSelect;
import net.minecraft.client.Minecraft;

public class ClientUtil {

    public static void openTargetSelection() {
        Minecraft.getInstance().setScreen(new HPScreenTargetSelect());
    }
}
