package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.troller2705.numismatics_subscriptions.AllBlocks;
import com.troller2705.numismatics_subscriptions.SubscriptionGuiTextures;
import dev.ithundxr.createnumismatics.base.client.rendering.GuiBlockEntityRenderBuilder;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.backend.behaviours.SliderStylePriceConfigurationPacket;
import dev.ithundxr.createnumismatics.util.TextUtils;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SubscriptionManagerScreen extends AbstractSimiContainerScreen<SubscriptionManagerMenu> {
    private EditBox labelBox;

    private final SubscriptionGuiTextures background = SubscriptionGuiTextures.SUBSCRIPTION_MANAGER;
    private final ItemStack renderedItem = AllBlocks.SUBSCRIPTION_MANAGER.asStack();

    private final int COIN_COUNT = Coin.values().length;

    private final Label[] coinLabels = new Label[COIN_COUNT];
    private final ScrollInput[] coinScrollInputs = new ScrollInput[COIN_COUNT];

    private List<Rect2i> extraAreas = Collections.emptyList();

    public SubscriptionManagerScreen(SubscriptionManagerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height + 2 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        setWindowOffset(-20, 0);
        super.init();

        int x = leftPos;
        int y = topPos;

        Consumer<String> onTextChanged = s -> labelBox.setX(nameBoxX(s, labelBox));
        labelBox = new EditBox(new NoShadowFontWrapper(font), x + 23, y + 4, background.width - 20, 10,
                Component.literal(menu.contentHolder.getLabelNonNull()));
        labelBox.setBordered(false);
        labelBox.setMaxLength(25);
        labelBox.setTextColor(0x592424);
        labelBox.setValue(menu.contentHolder.getLabelNonNull());
        labelBox.setFocused(false);
        labelBox.mouseClicked(0, 0, 0);
        labelBox.setResponder(onTextChanged);
        labelBox.setX(nameBoxX(labelBox.getValue(), labelBox));
        addRenderableWidget(labelBox);

        IconButton trustListButton = new IconButton(x + 7, y + background.height - 24, AllIcons.I_VIEW_SCHEDULE);
        trustListButton.withCallback(() -> {
//            menu.contentHolder.openTrustList();
        });
        addRenderableWidget(trustListButton);

        IconButton confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);

        for (Coin coin : Coin.values()) {
            int i = coin.ordinal();

            int baseX = x + 36 + (i < 3 ? 0 : 86) + 13;

            int yIncrement = 22;
            int baseY = y + 45 + (yIncrement * (i%3));

            coinLabels[i] = new Label(baseX + 18, baseY + 5, CommonComponents.EMPTY).withShadow();
            addRenderableWidget(coinLabels[i]);

            coinScrollInputs[i] = new ScrollInput(baseX, baseY, 36, 18)
                    .withRange(0, 129)
                    .writingTo(coinLabels[i])
                    .titled(Component.literal(TextUtils.titleCaseConversion(coin.getName(0))))
                    .calling((value) -> {
                        menu.contentHolder.setPrice(coin, value);
                        coinLabels[i].setX(baseX + 18 - font.width(coinLabels[i].text) / 2);
                    });
            addRenderableWidget(coinScrollInputs[i]);

            coinScrollInputs[i].setState(menu.contentHolder.getPrice(coin));
            coinScrollInputs[i].onChanged();
        }

        int baseX = x + 72;
        int baseY = y + background.height - 55;

        int baseX2 = baseX + 47;

        // Some example preset ranges for different options
        Map<String, Integer> timeOptionMaxValues = Map.of(
                "Ticks", 61,
                "Seconds", 61,
                "Minutes", 61,
                "Hours", 25
        );

        final Label timeLabel = new Label(baseX, baseY + 5, CommonComponents.EMPTY).withShadow();
        addRenderableWidget(timeLabel);

        final ScrollInput timeScrollInputs = new ScrollInput(baseX, baseY, 36, 18)
                .withRange(0, 61)
                .writingTo(timeLabel)
                .titled(Component.literal("Time Increment"))
                .calling((value) -> {
//                    menu.contentHolder.setPrice(coin, value);
                    timeLabel.setX(baseX + 18 - font.width(timeLabel.text) / 2);
                });
        addRenderableWidget(timeScrollInputs);

//        timeScrollInputs.setState(menu.contentHolder.getPrice(coin));
        timeScrollInputs.onChanged();


        final String[] timeOptions = {"Ticks", "Secs", "Mins", "Hrs"};

        final Label timeTypeLabel = new Label(baseX2, baseY + 5, CommonComponents.EMPTY).withShadow();
        addRenderableWidget(timeTypeLabel);

        final ScrollInput timeTypeScrollInputs = new ScrollInput(baseX2, baseY, 36, 18)
                .withRange(0, timeOptions.length)
                .writingTo(timeTypeLabel)
                .titled(Component.literal("Time Unit"))
                .calling((value) -> {
                    String text = timeOptions[value];
                    timeTypeLabel.text = Component.literal(text);
                    timeTypeLabel.setX(baseX2 + 18 - font.width(timeTypeLabel.text) / 2);

                    // Change the range of time value input based on selected unit
                    int maxValue = timeOptionMaxValues.getOrDefault(text, 1);
                    timeScrollInputs.withRange(0, maxValue);

                    // Reset state if out of bounds
                    if (timeScrollInputs.getState() > maxValue)
                        timeScrollInputs.setState(maxValue);

                    timeScrollInputs.onChanged();
                });
        addRenderableWidget(timeTypeScrollInputs);

        // Set default state and call update
        timeTypeScrollInputs.setState(0);
        timeTypeScrollInputs.onChanged();

        int baseX3 = baseX + 25;
        int baseY2 = y + background.height - 19;

        final String[] accountOptions = {"Joint", "Single"};

        final Label accountTypeLabel = new Label(baseX3, baseY2, CommonComponents.EMPTY).withShadow();
        addRenderableWidget(accountTypeLabel);

        final ScrollInput accountTypeScrollInputs = new ScrollInput(baseX3, baseY2, 36, 18)
                .withRange(0, accountOptions.length)
                .writingTo(accountTypeLabel)
                .titled(Component.literal("Account Type"))
                .calling((value) -> {
                    String text = accountOptions[value];
                    accountTypeLabel.text = Component.literal(text);
                    accountTypeLabel.setX(baseX3 + 18 - font.width(accountTypeLabel.text) / 2);
                });
        addRenderableWidget(accountTypeScrollInputs);

        // Set default state and call update
        accountTypeScrollInputs.setState(0);
        accountTypeScrollInputs.onChanged();

        extraAreas = ImmutableList.of(new Rect2i(x + background.width, y + background.height - 68, 84, 84));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (getFocused() != labelBox) {
            labelBox.setCursorPosition(labelBox.getValue()
                    .length());
            labelBox.setHighlightPos(labelBox.getCursorPosition());
        }
//        toggleExtractionIndicator.state = menu.contentHolder.allowExtraction() ? Indicator.State.GREEN : Indicator.State.RED;
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }

    @Override
    protected void renderForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
        int y = topPos;

        String text = labelBox.getValue();

        if (!labelBox.isFocused())
            AllGuiTextures.STATION_EDIT_NAME.render(graphics, nameBoxX(text, labelBox) + font.width(text) + 5, y + 1);
    }


    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.height + 2;
        renderPlayerInventory(graphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(graphics, x, y);

        GuiGameElement.of(renderedItem).<GuiGameElement
                        .GuiRenderBuilder>at(x + background.width + 6, y + background.height - 70, -200)
                .scale(5)
                .render(graphics);

        GuiBlockEntityRenderBuilder.of(menu.contentHolder)
                .<GuiGameElement
                        .GuiRenderBuilder>at(x + background.width + 6, y + background.height - 70, -230)
                .scale(5)
                .render(graphics);

//        graphics.drawCenteredString(font, title, x + (background.width - 8) / 2, y + 3, 0xFFFFFF);

        Couple<Integer> cogsAndSpurs = Coin.COG.convert(menu.contentHolder.getTotalPrice());
        int cogs = cogsAndSpurs.getFirst();
        int spurs = cogsAndSpurs.getSecond();
        Component balanceLabel = Component.translatable("block.numismatics.brass_depositor.tooltip.price",
                TextUtils.formatInt(cogs), Coin.COG.getName(cogs), spurs);
        graphics.drawCenteredString(font, balanceLabel, x + (background.width - 8) / 2, y + 21, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!labelBox.isFocused() && pMouseY > topPos && pMouseY < topPos + 14 && pMouseX > leftPos
                && pMouseX < leftPos + background.width) {
            labelBox.setFocused(true);
            labelBox.setHighlightPos(0);
            setFocused(labelBox);
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hitEnter = getFocused() instanceof EditBox
                && (pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER);

        if (hitEnter && labelBox.isFocused()) {
            if (labelBox.getValue().isEmpty())
                labelBox.setValue("Subscription Manager");
            labelBox.setFocused(false);
            syncName();
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void syncName() {
        if (!labelBox.getValue().equals(menu.contentHolder.getLabel()))
            setLabel(labelBox.getValue());
    }

    private void setLabel(String label) {
        CatnipServices.NETWORK.sendToServer(new SubscriptionManagerEditPacket(menu.contentHolder.getBlockPos(), label));
    }

    @Override
    public void removed() {
        CatnipServices.NETWORK.sendToServer(new SliderStylePriceConfigurationPacket(menu.contentHolder));
        super.removed();
        if (labelBox == null)
            return;
        syncName();
    }

    private int nameBoxX(String s, EditBox nameBox) {
        return leftPos + background.width / 2 - (Math.min(font.width(s), nameBox.getWidth()) + 10) / 2;
    }
}