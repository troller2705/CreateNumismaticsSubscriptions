package com.troller2705.numismatics_subscriptions;

import com.troller2705.numismatics_subscriptions.content.subscription_depositor.SubscriptionDepositorMenu;
import com.troller2705.numismatics_subscriptions.content.subscription_depositor.SubscriptionDepositorScreen;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerMenu;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class AllMenuTypes
{

    public static final MenuEntry<SubscriptionManagerMenu> SUBSCRIPTION_MANAGER = register(
            "subscription_manager",
            SubscriptionManagerMenu::new,
            () -> SubscriptionManagerScreen::new
    );

    public static final MenuEntry<SubscriptionDepositorMenu> SUBSCRIPTION_DEPOSITOR = register(
            "subscription_depositor",
            SubscriptionDepositorMenu::new,
            () -> SubscriptionDepositorScreen::new
    );

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return NumismaticsSubscriptions.REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }


    public static void initialize(){}
}
