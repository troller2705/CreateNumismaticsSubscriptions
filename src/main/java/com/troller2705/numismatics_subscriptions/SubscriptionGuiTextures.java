package com.troller2705.numismatics_subscriptions;

import com.mojang.blaze3d.systems.RenderSystem;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/*
Copied from Create
 */
public enum SubscriptionGuiTextures implements ScreenElement
{

    SUBSCRIPTION_DEPOSITOR("subscription_depositor", 208, 61),
    SUBSCRIPTION_MANAGER("subscription_manager", 208, 166),
    SUBS_LIST("subs_list", 208, 187),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    private SubscriptionGuiTextures(String location, int width, int height)
    {
        this(location, 0, 0, width, height);
    }

    private SubscriptionGuiTextures(int startX, int startY)
    {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private SubscriptionGuiTextures(String location, int startX, int startY, int width, int height)
    {
        this(NumismaticsSubscriptions.MODID, location, startX, startY, width, height);
    }

    private SubscriptionGuiTextures(String namespace, String location, int startX, int startY, int width, int height)
    {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    public void bind()
    {
        RenderSystem.setShaderTexture(0, location);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y)
    {
        bind();
        graphics.blit(location, x, y, 0, startX, startY, width, height, 256, 256);
    }

    public void render(GuiGraphics graphics, int x, int y, Color c)
    {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}
