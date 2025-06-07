package com.zeml.rotp_zhp.client.ui.screen;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.network.ButtonClickPacket;
import com.zeml.rotp_zhp.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.client.gui.widget.button.Button;


import java.util.*;

class HPTargetsList extends AbstractList<HPTargetsList.Entry> {
    private final Minecraft minecraft;
    private List<Object> originalList;
    private String filterText = "";
    private HPTargetsList initialList;

    public HPTargetsList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.minecraft = minecraft;
        this.setRenderBackground(false);
        this.originalList = new ArrayList<>();
        this.initialList = this;
    }

    public void updateList(Collection<?> newList) {
        this.clearEntries();
        this.originalList = new ArrayList<>(newList);
        newList.forEach(item -> this.addEntry(new Entry(item)));

        this.updateFilteredEntries();

    }

    private void updateFilteredEntries() {
        if (!Objects.equals(this.filterText, "")) {
            for (int i = this.getItemCount() - 1; i >= 0; i--) {
                Entry entry = this.getEntry(i);
                if (entry.item instanceof NetworkPlayerInfo) {
                    if (!((NetworkPlayerInfo) entry.item).getProfile().getName().toLowerCase(Locale.ROOT).contains(filterText.toLowerCase(Locale.ROOT))) {
                        this.remove(i);
                    }
                } else if (entry.item instanceof EntityType) {
                    if (!((EntityType<?>) entry.item).getDescription().getString().toLowerCase(Locale.ROOT).contains(filterText.toLowerCase(Locale.ROOT))) {
                        this.remove(i);
                    }
                } else if (entry.item instanceof Structure) {
                    if (!((Structure<?>) entry.item).getFeatureName().toLowerCase(Locale.ROOT).contains(filterText.toLowerCase(Locale.ROOT))) {
                        this.remove(i);
                    }
                } else if (entry.item instanceof StandType) {
                    if (!((StandType<?>) entry.item).getName().getString().toLowerCase(Locale.ROOT).contains(filterText.toLowerCase(Locale.ROOT))) {
                        this.remove(i);
                    }
                } else if (entry.item instanceof Biome) {
                    if (!biomeName((Biome) entry.item).toString().toLowerCase(Locale.ROOT).contains(filterText.toLowerCase(Locale.ROOT))) {
                        this.remove(i);
                    }
                }
            }
        }
    }


    public void setFilter(String filter) {
        this.filterText = filter.toLowerCase();

    }


    public boolean isEmpty() {
        return this.children().isEmpty();
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRowLeft()+204;
    }

    @Override
    protected void renderBackground(MatrixStack matrixStack) {
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();

        int rowLeft = this.getRowLeft();
        int rowTop = this.y0 + 4 - (int) this.getScrollAmount();

        int innerStartX = rowLeft-10 ;
        int innerStartY = this.y0 + 4-20 ;
        int innerEndX = innerStartX + 220;
        int innerEndY = innerStartY + 20;


        this.renderHeader(matrixStack, rowLeft, rowTop, tessellator);
        this.renderList(matrixStack, rowLeft, rowTop, mouseX, mouseY, partialTicks);



        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            RenderSystem.disableTexture();
            int barHeight = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            barHeight = MathHelper.clamp(barHeight, 32, this.y1 - this.y0 - 8);
            int barTop = (int) this.getScrollAmount() * (this.y1 - this.y0 - barHeight) / maxScroll + this.y0;
            if (barTop < this.y0) {
                barTop = this.y0;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);


            bufferbuilder.vertex((double) i, (double) this.y1, 0.0D).uv(0.0F, 1.0F).color(105, 75, 116, 255).endVertex();
            bufferbuilder.vertex((double) j, (double) this.y1, 0.0D).uv(1.0F, 1.0F).color(105, 75, 116, 255).endVertex();
            bufferbuilder.vertex((double) j, (double) this.y0, 0.0D).uv(1.0F, 0.0F).color(105, 75, 116, 255).endVertex();
            bufferbuilder.vertex((double) i, (double) this.y0, 0.0D).uv(0.0F, 0.0F).color(105, 75, 116, 255).endVertex();

            bufferbuilder.vertex((double) i, (double) (barTop + barHeight), 0.0D).uv(0.0F, 1.0F).color(182, 155, 207, 255).endVertex();
            bufferbuilder.vertex((double) j, (double) (barTop + barHeight), 0.0D).uv(1.0F, 1.0F).color(182, 155, 207, 255).endVertex();
            bufferbuilder.vertex((double) j, (double) barTop, 0.0D).uv(1.0F, 0.0F).color(182, 155, 207, 255).endVertex();
            bufferbuilder.vertex((double) i, (double) barTop, 0.0D).uv(0.0F, 0.0F).color(182, 155, 207, 255).endVertex();

            bufferbuilder.vertex((double) i, (double) (barTop + barHeight - 1), 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((double) (j - 1), (double) (barTop + barHeight - 1), 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((double) (j - 1), (double) barTop, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((double) i, (double) barTop, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();


            tessellator.end();
        }

        this.renderDecorations(matrixStack, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    @Override
    public int getRowLeft() {
        return this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    @Override
    public int getRowWidth() {
        return 200;
    }

    @Override
    protected int getRowTop(int index) {
        return super.getRowTop(index) + 10;
    }



    @Override
    protected void renderList(MatrixStack matrixStack, int top, int left, int mouseX, int mouseY, float partialTicks) {
        int itemCount = this.getItemCount();



        for (int i = 0; i < itemCount; ++i) {
            int rowTop = this.getRowTop(i);
            int rowBottom = rowTop + this.itemHeight;
            if (rowBottom >= this.y0+35 && rowTop <= this.y1-20) {
                this.renderItem(matrixStack, i, left, rowTop, mouseX, mouseY, partialTicks);
            }
        }
    }

    protected void renderItem(MatrixStack matrixStack, int index, int left, int top, int mouseX, int mouseY, float partialTicks) {
        Entry entry = this.getEntry(index);
        entry.render(matrixStack, index, top, left, this.getRowWidth(), this.itemHeight, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        for (Entry entry : this.children()) {
            if (entry.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }

        for (Entry entry : this.children()) {
            if (entry.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }

        for (Entry entry : this.children()) {
            if (entry.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }

        return false;
    }

    class Entry extends AbstractList.AbstractListEntry<Entry> {
        private final Object item;
        private final HPButton button;

        public Entry(Object item) {
            this.item = item;
            this.button = new HPButton (width / 2 + 50, 0, 20, 20, new StringTextComponent(""), button ->onButtonClick());

        }

        public Object getItem() {
            return item;
        }

        private void onButtonClick() {
            if (item instanceof NetworkPlayerInfo) {
                ModNetwork.sendToServer(new ButtonClickPacket(1,((NetworkPlayerInfo) item).getProfile().getName()));

            } else if (item instanceof EntityType) {
                ModNetwork.sendToServer(new ButtonClickPacket(2,((EntityType<?>) item).getRegistryName().toString()));

            } else if (item instanceof Structure) {
                ModNetwork.sendToServer(new ButtonClickPacket(3,((Structure<?>) item).getRegistryName().toString()));
            } else if (item instanceof StandType<?>) {
                ModNetwork.sendToServer(new ButtonClickPacket(-1,((StandType<?>)item).getRegistryName().toString()));
            } else if (item instanceof Biome) {
                ModNetwork.sendToServer(new ButtonClickPacket(4,((Biome)item).getRegistryName().toString()));
            }
        }

        @Override
        public void render(MatrixStack matrixStack, int index, int top, int left, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
            if (item instanceof NetworkPlayerInfo) {
                minecraft.font.draw(matrixStack, ((NetworkPlayerInfo) item).getProfile().getName(), getRowLeft()+10, top, 0xFFFFFF);

                if(IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation() instanceof HermitPurpleEntity){
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation();
                    if(((NetworkPlayerInfo) item).getProfile().getName().equals(hermitPurple.getTarget())){
                        button.setTarget(true);
                    }
                }

            } else if (item instanceof EntityType) {
                minecraft.font.draw(matrixStack, ((EntityType<?>) item).getDescription().getString(), getRowLeft()+10, top, 0xFFFFFF);

                if(IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation() instanceof HermitPurpleEntity){
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation();
                    if(((EntityType<?>) item).getDescription().getString().equals(hermitPurple.getTarget())){
                        button.setTarget(true);
                    }
                }

            } else if (item instanceof Structure) {
                minecraft.font.draw(matrixStack, ((Structure<?>) item).getFeatureName(), getRowLeft()+10, top, 0xFFFFFF);

                if(IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation() instanceof HermitPurpleEntity){
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation();
                    if(((Structure<?>) item).getFeatureName().equals(hermitPurple.getTarget())){
                        button.setTarget(true);
                    }
                }

            } else if (item instanceof StandType<?>) {
                minecraft.font.draw(matrixStack,((StandType<?>)item).getName(),getRowLeft()+10, top, 0xFFFFFF);

                if(IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation() instanceof HermitPurpleEntity){
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation();
                    if(((StandType<?>)item).getName().equals(hermitPurple.getTarget())){
                        button.setTarget(true);
                    }
                }

            } else if (item instanceof Biome) {
                minecraft.font.draw(matrixStack,biomeName((Biome) item),getRowLeft()+10, top, 0xFFFFFF);

                if(IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation() instanceof HermitPurpleEntity){
                    HermitPurpleEntity hermitPurple = (HermitPurpleEntity) IStandPower.getPlayerStandPower(minecraft.player).getStandManifestation();
                    if(biomeName((Biome) item).equals(hermitPurple.getTarget())){
                        button.setTarget(true);
                    }
                }

            }



            this.button.y = top;
            this.button.render(matrixStack, mouseX, mouseY, partialTicks);
        }


        public List<? extends IGuiEventListener> children() {
            return ImmutableList.of(this.button);
        }


        public List<? extends IRenderable> renderables() {
            return ImmutableList.of(this.button);
        }


        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.button.mouseClicked(mouseX, mouseY, button)) {
                this.onButtonClick();
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.button.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            return this.button.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public TranslationTextComponent biomeName(Biome item){
        return new TranslationTextComponent(Util.makeDescriptionId("biome",item.getRegistryName()));
    }

}