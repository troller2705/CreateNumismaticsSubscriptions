package com.troller2705.numismatics_subscriptions;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = NumismaticsSubscriptions.MODID, dist = Dist.CLIENT)
public class NumismaticsSubscriptionsClient {

    public NumismaticsSubscriptionsClient(ModContainer modContainer){

        AllPartialModels.initialize();
        AllCreativeTabs.initialize();

    }

}
