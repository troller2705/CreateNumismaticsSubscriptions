package com.troller2705.numismatics_subscriptions;

import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.registry.NumismaticsCreativeModeTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AllCreativeTabs {

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = NumismaticsSubscriptions.CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.numismatics_subscriptions.creative_tab"))
            .icon(AllBlocks.SUBSCRIPTION_DEPOSITOR::asStack)
            .withTabsBefore(NumismaticsCreativeModeTabs.Tabs.MAIN.getKey())
            .displayItems((itemDisplayParameters, output) -> {

                output.accept(AllBlocks.SUBSCRIPTION_MANAGER.asStack());
                output.accept(AllBlocks.SUBSCRIPTION_DEPOSITOR.asStack());

            })
            .build());

    public static void initialize(){

    }

}
