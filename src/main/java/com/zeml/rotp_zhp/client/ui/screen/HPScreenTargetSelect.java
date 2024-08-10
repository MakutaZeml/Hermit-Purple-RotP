package com.zeml.rotp_zhp.client.ui.screen;


import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.zeml.rotp_zhp.RotpHermitPurpleAddon;
import com.zeml.rotp_zhp.network.ButtonClickPacket;
import com.zeml.rotp_zhp.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class HPScreenTargetSelect extends Screen {
    protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation(RotpHermitPurpleAddon.MOD_ID,"/textures/gui/hp_target.png");
    private static final ITextComponent TAB_PLAYERS = new TranslationTextComponent("gui.hermitpurpletarget.tab_all");
    private static final ITextComponent TAB_ENTITIES = new TranslationTextComponent("gui.hermitpurpletarget.tab_hidden");
    private static final ITextComponent TAB_STRUCTURES = new TranslationTextComponent("gui.hermitpurpletarget.tab_blocked");
    private static final ITextComponent TAB_STANDS = new TranslationTextComponent("gui.hermitpurpletarget.tab_stands");
    private static final ITextComponent TAB_BIOMES = new TranslationTextComponent("gui.hermitpurpletarget.tab_biomes");
    private static final ITextComponent RANDOM = new TranslationTextComponent("gui.hermitpurpletarget.random");
    private static final ITextComponent TAB_ALL_SELECTED = TAB_PLAYERS.plainCopy().withStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent TAB_ENTITIES_SELECTED = TAB_ENTITIES.plainCopy().withStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent TAB_STRUCTURES_SELECTED = TAB_STRUCTURES.plainCopy().withStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent TAB_STANDS_SELECTED = TAB_STANDS.plainCopy().withStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent TAB_BIOMES_SELECTED = TAB_BIOMES.plainCopy().withStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent SEARCH_HINT = (new TranslationTextComponent("gui.socialInteractions.search_hint")).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
    private static final ITextComponent EMPTY_SEARCH = (new TranslationTextComponent("gui.hermitpurpletarget.search_empty")).withStyle(TextFormatting.GRAY);
    private static final ITextComponent EMPTY_ENTITIES = (new TranslationTextComponent("gui.socialInteractions.empty_entities")).withStyle(TextFormatting.GRAY);
    private static final ITextComponent EMPTY_STRUCTURES = (new TranslationTextComponent("gui.socialInteractions.empty_structures")).withStyle(TextFormatting.GRAY);
    private HPTargetsList HPTargetsList;
    private TextFieldWidget searchBox;
    private String lastSearch = "";
    private HPScreenTargetSelect.Mode page = HPScreenTargetSelect.Mode.PLAYERS;
    private HPButton allButton;
    private HPButton entitiesButton;
    private HPButton structuresButton;
    private HPButton randomButton;
    private HPButton standButton;
    private HPButton biomesButton;
    @Nullable
    private ITextComponent serverLabel;
    private int playerCount;
    private boolean initialized;
    @Nullable
    private Runnable postRenderRunnable;

    public HPScreenTargetSelect() {
        super(new TranslationTextComponent( "gui.hermitpurpletarget.title").withStyle(TextFormatting.DARK_PURPLE));
        this.updateServerLabel(Minecraft.getInstance());
    }

    private int windowHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int backgroundUnits() {
        return this.windowHeight() / 16;
    }

    private int listEnd() {
        return 80 + this.backgroundUnits() * 16 - 8;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }

    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.serverLabel.getString();
    }

    public void tick() {
        super.tick();
        this.searchBox.tick();

    }
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String searchString = this.searchBox.getValue();
        this.init(minecraft, width, height);
        this.searchBox.setValue(searchString);
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        if (this.initialized) {
            this.HPTargetsList.updateSize(this.width, this.height, 88, this.listEnd());
        } else {
            this.HPTargetsList = new HPTargetsList(this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
        }

        int i = 220 / 3;
        int j = this.HPTargetsList.getRowLeft();
        int k = this.HPTargetsList.getRowRight();
        int l = this.font.width(RANDOM) + 40;
        int i1 = 64 + 16 * this.backgroundUnits();
        int j1 = (this.width - l) / 2;

        this.allButton = this.addButton(new HPButton(j, 45, i, 20, TAB_PLAYERS, (p_244686_1_) -> {
            this.showPage(Mode.PLAYERS);
            this.HPTargetsList.setScrollAmount(0);
        }));
        this.entitiesButton = this.addButton(new HPButton((j + k - i) / 2 + 1, 45, i, 20, TAB_ENTITIES, (p_244681_1_) -> {
            this.showPage(Mode.ENTITIES);
            this.HPTargetsList.setScrollAmount(0);
        }));
        this.structuresButton = this.addButton(new HPButton(k - i + 1, 45, i, 20, TAB_STRUCTURES, (p_244769_1_) -> {
            this.showPage(Mode.STRUCTURES);
            this.HPTargetsList.setScrollAmount(0);
        }));
        this.standButton = this.addButton(new HPButton(j,i1+15,i,20,TAB_STANDS,(buttons)->{
            this.showPage(Mode.STAND);
            this.HPTargetsList.setScrollAmount(0);
        }));
        this.biomesButton = this.addButton(new HPButton(k - i + 1,i1+15,i,20,TAB_BIOMES,(buttons)->{
            this.showPage(Mode.BIOMES);
            this.HPTargetsList.setScrollAmount(0);
        }));
        this.randomButton = this.addButton(new HPButton(j1, i1, l, 20, RANDOM, (p_244767_1_)-> {
            ModNetwork.sendToServer(new ButtonClickPacket(0,"random"));
        }));


        String s = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new TextFieldWidget(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_HINT) {
            protected IFormattableTextComponent createNarrationMessage() {
                return !HPScreenTargetSelect.this.searchBox.getValue().isEmpty() && HPScreenTargetSelect.this.HPTargetsList.isEmpty() ? super.createNarrationMessage().append(", ").append(HPScreenTargetSelect.EMPTY_SEARCH) : super.createNarrationMessage();
            }
        };

        this.searchBox.setMaxLength(16);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(s);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.children.add(this.searchBox);
        this.children.add(this.HPTargetsList);
        this.initialized = true;
        this.showPage(this.page);
    }


    private void showPage(HPScreenTargetSelect.Mode mode) {
        this.page = mode;
        this.allButton.setMessage(TAB_PLAYERS);
        this.entitiesButton.setMessage(TAB_ENTITIES);
        this.structuresButton.setMessage(TAB_STRUCTURES);
        this.standButton.setMessage(TAB_STANDS);

        Collection<?> collection;
        switch (mode) {
            case PLAYERS:
                this.allButton.setMessage(TAB_ALL_SELECTED);
                collection = this.minecraft.player.connection.getOnlinePlayers();
                break;
            case ENTITIES:
                this.entitiesButton.setMessage(TAB_ENTITIES_SELECTED);
                collection = ForgeRegistries.ENTITIES.getValues().stream()
                        .filter(entityType -> entityType.getCategory() != EntityClassification.MISC)
                        .collect(Collectors.toList());
                break;
            case STRUCTURES:
                this.structuresButton.setMessage(TAB_STRUCTURES_SELECTED);
                collection = ForgeRegistries.STRUCTURE_FEATURES.getValues();
                break;
            case STAND:
                this.standButton.setMessage(TAB_STANDS_SELECTED);
                collection = StandUtil.availableStands(this.minecraft.player.level.isClientSide).collect(Collectors.toList());
                break;
            case BIOMES:
                this.biomesButton.setMessage(TAB_BIOMES_SELECTED);
                collection = ForgeRegistries.BIOMES.getValues();
                break;
            default:
                collection = Collections.emptyList();
        }

        this.HPTargetsList.updateList(collection);
        if (!this.searchBox.getValue().isEmpty() && this.HPTargetsList.isEmpty() && !this.searchBox.isFocused()) {
            NarratorChatListener.INSTANCE.sayNow(EMPTY_SEARCH.getString());
        } else if (collection.isEmpty()) {
            switch (mode) {
                case ENTITIES:
                    NarratorChatListener.INSTANCE.sayNow(EMPTY_ENTITIES.getString());
                    break;
                case STRUCTURES:
                    NarratorChatListener.INSTANCE.sayNow(EMPTY_STRUCTURES.getString());
                    break;
            }
        }
    }


    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }



    public void renderBackground(MatrixStack matrixStack) {
        int i = this.marginX() + 3;
        super.renderBackground(matrixStack);

        AtomicReference<ResourceLocation> texture = new AtomicReference<>(SOCIAL_INTERACTIONS_LOCATION);

        IStandPower.getStandPowerOptional(minecraft.player).ifPresent(power -> {
            texture.set(StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                    .getStandSkin(power.getStandInstance().get()), SOCIAL_INTERACTIONS_LOCATION));
        });

        this.minecraft.getTextureManager().bind(texture.get());
        this.blit(matrixStack, i, 64, 1, 1, 236, 8);
        int j = this.backgroundUnits();

        for (int k = 0; k < j; ++k) {
            this.blit(matrixStack, i, 72 + 16 * k, 1, 10, 236, 16);
        }

        this.blit(matrixStack, i, 72 + 16 * j, 1, 27, 236, 8);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.HPTargetsList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 16777215);
        this.searchBox.render(matrixStack, mouseX, mouseY, partialTicks);

        if(!this.HPTargetsList.isEmpty()){
            this.HPTargetsList.render(matrixStack,mouseX,mouseY,partialTicks);
        } else if (! this.searchBox.getValue().isEmpty()) {
            drawCenteredString(matrixStack, this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
        }else {
            switch (this.page){
                case ENTITIES:
                    drawCenteredString(matrixStack, this.minecraft.font, EMPTY_ENTITIES, this.width / 2, (78 + this.listEnd()) / 2, -1);
                    break;
                case STRUCTURES:
                    drawCenteredString(matrixStack, this.minecraft.font, EMPTY_STRUCTURES, this.width / 2, (78 + this.listEnd()) / 2, -1);
            }
            if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
                drawString(matrixStack, this.minecraft.font, SEARCH_HINT, this.searchBox.x, this.searchBox.y, -1);
            } else {
                this.searchBox.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        int i = this.marginX();
        int j = this.font.width(this.serverLabel);
        this.font.draw(matrixStack, this.serverLabel, (float) (i + 230 - j), 52.0F, -8355712);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.postRenderRunnable != null) {
            this.postRenderRunnable.run();
        }

        if(searchBox.getValue().isEmpty()){
            drawCenteredString(matrixStack,this.minecraft.font, SEARCH_HINT, this.searchBox.x+21, this.searchBox.y,-1);
        }
        this.randomButton.render(matrixStack,mouseX, mouseY, partialTicks);

    }

    private void updateServerLabel(Minecraft p_244680_1_) {
        int i = p_244680_1_.getConnection().getOnlinePlayers().size();
        if (this.playerCount != i) {
            String s = "";
            ServerData serverdata = p_244680_1_.getCurrentServer();
            if (p_244680_1_.isLocalServer()) {
                s = p_244680_1_.getSingleplayerServer().getMotd();
            } else if (serverdata != null) {
                s = serverdata.name;
            }

            if (i > 1) {
                this.serverLabel = new TranslationTextComponent("gui.socialInteractions.server_label.multiple", s, i);
            } else {
                this.serverLabel = new TranslationTextComponent("gui.socialInteractions.server_label.single", s, i);
            }

            this.playerCount = i;
        }

    }

    private void checkSearchStringUpdate(String searchString) {
        searchString = searchString.toLowerCase(Locale.ROOT);
        if (!searchString.equals(this.lastSearch)) {
            this.HPTargetsList.setFilter(searchString);
            this.HPTargetsList.setScrollAmount(0);
            this.lastSearch = searchString;
            this.showPage(this.page);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    enum Mode {
        PLAYERS,
        ENTITIES,
        STRUCTURES,
        STAND,
        BIOMES;
    }
}