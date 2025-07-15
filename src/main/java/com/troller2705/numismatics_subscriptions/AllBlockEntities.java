package com.troller2705.numismatics_subscriptions;

import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerBlockEntity;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.troller2705.numismatics_subscriptions.content.subscription_depositor.SubscriptionDepositorBlockEntity;

public class AllBlockEntities {

    public static final BlockEntityEntry<SubscriptionDepositorBlockEntity> SUBSCRIPTION_DEPOSITOR = NumismaticsSubscriptions.REGISTRATE.blockEntity("subscription_depositor", SubscriptionDepositorBlockEntity::new)
            .validBlocks(AllBlocks.SUBSCRIPTION_DEPOSITOR)
            .register();

    public static final BlockEntityEntry<SubscriptionManagerBlockEntity> SUBSCRIPTION_MANAGER = NumismaticsSubscriptions.REGISTRATE.blockEntity("subscription_manager", SubscriptionManagerBlockEntity::new)
            .validBlocks(AllBlocks.SUBSCRIPTION_MANAGER)
            .renderer(() -> SubscriptionManagerRenderer::new)
            .register();

    public static void initialize(){}
}
