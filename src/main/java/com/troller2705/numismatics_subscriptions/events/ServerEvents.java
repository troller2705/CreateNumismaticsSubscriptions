package com.troller2705.numismatics_subscriptions.events;

import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber()
public class ServerEvents {

    private static byte ticks = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post tickEvent) {
        ticks += 1;
        if(ticks > 20){
            ticks = 0;

            NumismaticsSubscriptions.BANK.SUBSCRIPTION_PROCESSOR.run(tickEvent.getServer());
        }
    }
}
