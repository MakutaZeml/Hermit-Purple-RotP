package com.zeml.rotp_zhp.client.ui.screen;

import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.client.ui.screen.widgets.CustomButton;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("deprecation")
public class HPButton extends CustomButton {
    protected static final ResourceLocation BUTTON = new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"/textures/gui/hp_widgets.png");

    private boolean isTarget;

    public HPButton(int x, int y, int width, int height,
                             ITextComponent message, IPressable onPress) {
        super(x, y, width, height, message, onPress);
        this.setMessage(message);
    }

    public HPButton(int x, int y, int width, int height,
                             ITextComponent message, IPressable onPress, ITooltip tooltip) {
        super(x, y, width, height, message, onPress, tooltip);
    }

    @Override
    protected void renderCustomButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        ResourceLocation texture = BUTTON;


        if(IStandPower.getPlayerStandPower(minecraft.player).getStandInstance().isPresent()){
            texture = StandSkinsManager.getInstance().getRemappedResPath(manager ->
                    manager.getStandSkin(IStandPower.getPlayerStandPower(minecraft.player).getStandInstance().get()),BUTTON);
        }



        minecraft.getTextureManager().bind(texture);
        FontRenderer fontrenderer = minecraft.font;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(isHovered() || isTarget());
        blit(matrixStack, x, y, 0, 46 + i * 20, width / 2, height);
        blit(matrixStack, x + width / 2, y, 200 - width / 2, 46 + i * 20, width / 2, height);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);

    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean target) {
        isTarget = target;
    }
}
