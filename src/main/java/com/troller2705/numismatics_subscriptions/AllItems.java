package com.troller2705.numismatics_subscriptions;


import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionGuideItem;
import com.tterrag.registrate.util.entry.ItemEntry;

public class AllItems {

    public static final ItemEntry<SubscriptionGuideItem> SUBSCRIPTION_GUIDE = NumismaticsSubscriptions.REGISTRATE.item("subscription_guide", SubscriptionGuideItem::new)
            .lang("Subscription Guide")
            .register();



    public static void initialize(){
    }
}
