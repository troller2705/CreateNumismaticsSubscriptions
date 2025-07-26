package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.troller2705.numismatics_subscriptions.SubscriptionGuiTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static javax.swing.plaf.basic.BasicGraphicsUtils.drawString;

public class SubsListScreen extends AbstractSimiContainerScreen<SubsListMenu> {
    private final SubscriptionGuiTextures background = SubscriptionGuiTextures.SUBSCRIPTION_DEPOSITOR;
    private final List<String[]> stringRows = new ArrayList<>();
    private final int rowHeight = 12;
    private int scrollOffset = 0;
    private int maxScroll;
    private int viewHeight;

    public SubsListScreen(SubsListMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        // Fake data
        for (int i = 0; i < 50; i++) {
            stringRows.add(new String[]{"Label " + i, "Value " + i});
        }

        viewHeight = imageHeight - 40;
        maxScroll = Math.max(0, stringRows.size() * rowHeight - viewHeight);

        // Optional: add Create-style scroll input
        int maxIndex = maxScroll / rowHeight;

        ScrollInput scrollInput = new ScrollInput(leftPos + imageWidth - 20, topPos + 20, 14, 80)
                .withRange(0, maxIndex)
                .titled(Component.literal("")) // Empty title disables most of the tooltip text
                .calling(i -> scrollOffset = (maxIndex - i) * rowHeight); // Inverted!
        scrollInput.setTooltip(Tooltip.create(Component.literal("")));

        scrollInput.setState(maxIndex);

        addRenderableWidget(scrollInput);

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.height + 2;
        renderPlayerInventory(guiGraphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(guiGraphics, x, y);
        // Clipping region for scrollable area
        guiGraphics.enableScissor(leftPos + 10, topPos + 20, leftPos + imageWidth - 10, topPos + imageHeight - 20);

        int startY = topPos + 20 - scrollOffset;
        for (int i = 0; i < stringRows.size(); i++) {
            y = startY + i * rowHeight;
            if (y + rowHeight > topPos + imageHeight - 20 || y < topPos + 20)
                continue;

            String[] row = stringRows.get(i);
            guiGraphics.drawString(font, row[0], leftPos + 20, y, 0xFFFFFF, false);
            guiGraphics.drawString(font, row[1], leftPos + imageWidth / 2, y, 0xAAAAAA, false);
            guiGraphics.hLine(leftPos + 10, leftPos + imageWidth - 10, y + rowHeight - 2, 0x333333);
        }

        guiGraphics.disableScissor();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta * rowHeight));
        return true;
    }

}
