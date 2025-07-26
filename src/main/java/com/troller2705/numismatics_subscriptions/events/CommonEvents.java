package com.troller2705.numismatics_subscriptions.events;

import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void onWorldJoin(LevelEvent.Load event){
        NumismaticsSubscriptions.BANK.levelLoaded(event.getLevel());
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        NumismaticsSubscriptions.BANK.markBankDirty();
    }

}
