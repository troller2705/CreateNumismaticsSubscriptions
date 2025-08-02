package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.troller2705.numismatics_subscriptions.AllBlockEntities;
import com.troller2705.numismatics_subscriptions.AllBlocks;
import com.troller2705.numismatics_subscriptions.AllItems;
import com.troller2705.numismatics_subscriptions.SubscriptionGuiTextures;
import com.troller2705.numismatics_subscriptions.content.backend.SubscriptionStatus;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerBlockEntity;
import dev.ithundxr.createnumismatics.base.client.rendering.GuiBlockEntityRenderBuilder;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubsListScreen extends AbstractSimiContainerScreen<SubsListMenu> {
    private final SubscriptionGuiTextures background = SubscriptionGuiTextures.SUBS_LIST;
    private final List<String[]> stringRows = new ArrayList<>();
    private final int rowHeight = 12;
    private int scrollOffset = 0;
    private int maxScroll;

    private final ItemStack renderedItem = AllItems.SUBSCRIPTION_GUIDE.asStack();

    private List<SubscriptionStatus> subs = new ArrayList<>();

    private ScrollInput scrollInput;

    private List<Rect2i> extraAreas = Collections.emptyList();

    public SubsListScreen(SubsListMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height + 2 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        setWindowOffset(-20, 0);
        super.init();

        int x = leftPos;
        int y = topPos;

        IconButton confirmButton = new IconButton(leftPos + background.width - 41, topPos + background.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);

        extraAreas = ImmutableList.of(new Rect2i(x + background.width, y + background.height - 64, 84, 74));
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.height + 2;
        renderPlayerInventory(guiGraphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(guiGraphics, x, y);

        GuiGameElement.of(renderedItem).<GuiGameElement
                        .GuiRenderBuilder>at(x + background.width + 6, y + background.height - 70, -200)
                .scale(5)
                .render(guiGraphics);

        guiGraphics.drawCenteredString(font, title, x + (background.width - 8) / 2, y + 3, 0xFFFFFF);
        guiGraphics.drawString(font, "User", leftPos + (imageWidth / 2) / 2 - 10, y + 20, 0x000000, false);
        guiGraphics.drawString(font, "Validity", leftPos + imageWidth / 2 + 20, y + 20, 0x000000, false);
        // Clipping region for scrollable area
        guiGraphics.enableScissor(leftPos + 10, topPos + 40, leftPos + background.width - 10, topPos + background.height - 31);

        int startY = topPos + 40 - scrollOffset;
        for (int i = 0; i < stringRows.size(); i++) {
            y = startY + i * rowHeight;
            if (y + rowHeight > topPos + imageHeight - 20 || y < topPos + 20)
                continue;

            String[] row = stringRows.get(i);
            int textStartX = leftPos + 20;
            int dividerX = leftPos + imageWidth / 2;
            int availableWidth = dividerX - textStartX - 4; // leave some padding

            String name = row[0];
            String displayName = font.plainSubstrByWidth(name, availableWidth);
            if (!displayName.equals(name)) {
                displayName = font.plainSubstrByWidth(name, availableWidth - font.width("...")) + "...";
            }

            guiGraphics.drawString(font, displayName, textStartX, y, 0x000000, false);
            if (row[1].equals("Valid")) guiGraphics.drawString(font, row[1], leftPos + imageWidth / 2 + 20, y, 0x00FF00, false);
            else guiGraphics.drawString(font, row[1], leftPos + imageWidth / 2 + 20, y, 0xFF0000, false);
        }
        guiGraphics.disableScissor();
        x = leftPos + imageWidth / 2;
        guiGraphics.fill(x, topPos + 37, x + 1, topPos + background.height - 30, 0xFF000000);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double pScrollX, double pScrollY) {
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - pScrollY * rowHeight));
        return true;
    }

    public void receiveProfiles(List<SubscriptionStatus> profiles)
    {
        this.subs = profiles;
        for (SubscriptionStatus sub : this.subs)
        {
            String val = sub.isSubscribed() ? "Valid" : "Invalid";
            stringRows.add(new String[]{sub.name(), val});
        }

        maxScroll = Math.max(0, stringRows.size() * rowHeight - 120);
    }

}
