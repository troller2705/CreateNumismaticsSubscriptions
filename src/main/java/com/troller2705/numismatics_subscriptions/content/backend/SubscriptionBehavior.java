package com.troller2705.numismatics_subscriptions.content.backend;

import dev.ithundxr.createnumismatics.content.backend.Coin;

import java.util.Map;
import java.util.UUID;

public interface SubscriptionBehavior {

    int getInterval();
    void setInterval(int interval);

    String getUnit();
    void setUnit(String unit);

    int getPrice(Coin coin);
    void setPrice(Coin coin, int price);
    int[] getPrices();
    int getTotalPrice();

    String getAllowedAccountType();
    void  setAllowedAccountType(String allowedAccountType);

    Map<UUID, Boolean> getSubscribers();
    void addSubscriber(UUID uuid);
    void setSubscriber(UUID uuid, boolean isValid);
    void removeSubscriber(UUID uuid);

}
