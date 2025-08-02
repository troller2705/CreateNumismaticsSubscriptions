package com.troller2705.numismatics_subscriptions.content.backend;

import com.troller2705.numismatics_subscriptions.AllConstants;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import dev.ithundxr.createnumismatics.Numismatics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubscriptionProcessor {

    private final HashMap<UUID, HashMap<UUID, Long>> active_subscriptions = new HashMap<>();

    public void initialize(){
        active_subscriptions.clear();

        final long timestamp = System.currentTimeMillis();

        for (ExtendedAccountData extAcc : NumismaticsSubscriptions.BANK.extendedAccounts.values()) {
            active_subscriptions.put(extAcc.id, new HashMap<>());
            var currentSubscription = active_subscriptions.get(extAcc.id);

            for (UUID subscriberUUID : extAcc.getSubscribers().keySet()){
                currentSubscription.put(subscriberUUID, timestamp);
            }
        }
    }

    public void run(MinecraftServer server){

        final long timestamp = System.currentTimeMillis();

        // Remove deleted subscriptions
        for (UUID activeUUID : active_subscriptions.keySet()){
            if(!NumismaticsSubscriptions.BANK.extendedAccounts.containsKey(activeUUID)){
                active_subscriptions.remove(activeUUID);
            }
        }

        for (ExtendedAccountData extAcc : NumismaticsSubscriptions.BANK.extendedAccounts.values()){

            // Add new subscriptions
            active_subscriptions.putIfAbsent(extAcc.id, new HashMap<>());
            var currentSubscription = active_subscriptions.get(extAcc.id);

            for (Map.Entry<UUID, Boolean> subscriber : extAcc.getSubscribers().entrySet()){

                // Skip invalid accounts
                if(!subscriber.getValue()) continue;

                // Add new subscribers
                var currentSubscriber = currentSubscription.putIfAbsent(subscriber.getKey(), timestamp);

                // If new subscriber or existing over the interval
                if(currentSubscriber == null || currentSubscriber + getIntervalMillis(extAcc) <= timestamp){

                    currentSubscription.put(subscriber.getKey(), timestamp);

                    if(transfer(extAcc, subscriber.getKey())){
                        extAcc.setSubscriber(subscriber.getKey(), true);
                        var player = server.getPlayerList().getPlayer(subscriber.getKey());
                        if(player != null){
                            var subName = Numismatics.BANK.getAccount(extAcc.id).getLabel();
                            player.displayClientMessage(Component.literal(String.format("Subscription '%s' was withdrawn", subName)), true);
                        }
                    }else{
                        extAcc.setSubscriber(subscriber.getKey(), false);
                    }
                }
            }

        }

    }

    private boolean transfer(ExtendedAccountData recipient, UUID subscriber){
        var price = recipient.getCoinPrice().getTotalPrice();
        var recBank = Numismatics.BANK.getAccount(recipient.id);
        var subBank = Numismatics.BANK.getAccount(subscriber);

        if(recBank == null || subBank == null) return false;

        if(subBank.deduct(price)){
            recBank.deposit(price);
            return true;
        }

        return false;
    }

    private long getIntervalMillis(ExtendedAccountData extendedAccountData){
        int multiplier = switch (extendedAccountData.getUnit()){
            case AllConstants.Time.SECONDS -> 1_000;
            case AllConstants.Time.MINUTES -> 60_000;
            case AllConstants.Time.HOURS -> 3_600_000;
            default -> throw new IllegalStateException("Unexpected value: " + extendedAccountData.getUnit());
        };


        return ((long)extendedAccountData.getInterval()) * multiplier;
    }

}
