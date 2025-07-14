package com.troller2705.createnumismaticssubs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AllCreativeTabs {

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = NumismaticsSubscriptions.CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.numismatics_subscriptions.creative_tab"))
            .icon(() -> AllBlocks.SUBSCRIPTION_DEPOSITOR.asStack())
            .build());

    public static void initialize(){

    }

}
