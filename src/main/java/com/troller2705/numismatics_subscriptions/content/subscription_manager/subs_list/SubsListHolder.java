package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import net.minecraft.server.level.ServerPlayer;

public interface SubsListHolder
{
    /**
     * Opens the Subs List menu for the player
     * @param player will be checked for permission by the implementation
     */
    void openSubsListMenu(ServerPlayer player);
}
