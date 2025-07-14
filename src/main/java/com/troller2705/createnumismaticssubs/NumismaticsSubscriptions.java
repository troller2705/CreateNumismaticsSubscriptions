package com.troller2705.createnumismaticssubs;


import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(NumismaticsSubscriptions.MODID)
public class NumismaticsSubscriptions {
    public static String MODID = "numismatics_subscriptions";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    static {
        REGISTRATE
                .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public NumismaticsSubscriptions(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);


        REGISTRATE.defaultCreativeTab(AllCreativeTabs.MAIN, "main");

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        LOGGER.info("Registering create_colored blocks!");

    }
}
