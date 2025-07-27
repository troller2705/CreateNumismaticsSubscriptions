package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.mojang.authlib.GameProfile;
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
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javax.swing.plaf.basic.BasicGraphicsUtils.drawString;

public class SubsListScreen extends AbstractSimiContainerScreen<SubsListMenu> {
    private final SubscriptionGuiTextures background = SubscriptionGuiTextures.SUBSCRIPTION_DEPOSITOR;
    private final List<String[]> stringRows = new ArrayList<>();
    private final int rowHeight = 12;
    private int scrollOffset = 0;
    private int maxScroll;
    private int viewHeight;

    private List<Pair<GameProfile, Boolean>> subs = new ArrayList<>();

    private ScrollInput scrollInput;

    public SubsListScreen(SubsListMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;


        viewHeight = imageHeight - 40;
        maxScroll = Math.max(0, stringRows.size() * rowHeight - viewHeight);

        // Optional: add Create-style scroll input
        int maxIndex = maxScroll / rowHeight;

        scrollInput = new ScrollInput(leftPos + imageWidth - 20, topPos + 20, 14, 80)
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
            guiGraphics.drawString(font, row[1], leftPos + imageWidth / 2 + 20, y, 0xAAAAAA, false);
        }
        x = leftPos + imageWidth / 2;
        guiGraphics.fill(x, topPos + 10, x + 1, topPos + imageHeight - 10, 0xFFFFFFFF);
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

    public void receiveProfiles(List<Pair<GameProfile, Boolean>> profiles)
    {
        this.subs = profiles;
        for (Pair<GameProfile, Boolean> sub : this.subs)
        {
            String val = sub.getSecond() ? "Valid" : "Invalid";
            stringRows.add(new String[]{sub.getFirst().getName(), val});
        }

        viewHeight = imageHeight - 40;
        maxScroll = Math.max(0, stringRows.size() * rowHeight - viewHeight);

        // Optional: add Create-style scroll input
        int maxIndex = maxScroll / rowHeight;
        scrollInput.withRange(0, maxIndex);
        scrollInput.setState(maxIndex);
    }

}
