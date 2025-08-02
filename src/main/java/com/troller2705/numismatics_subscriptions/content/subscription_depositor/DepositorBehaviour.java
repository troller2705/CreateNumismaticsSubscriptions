package com.troller2705.numismatics_subscriptions.content.subscription_depositor;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.AllConstants;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import com.troller2705.numismatics_subscriptions.content.backend.CoinPrice;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedAccountData;
import com.troller2705.numismatics_subscriptions.content.backend.SubscriptionBehavior;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class DepositorBehaviour extends BlockEntityBehaviour implements SubscriptionBehavior {

    public static final BehaviourType<DepositorBehaviour> TYPE = new BehaviourType<>();

    protected final Supplier<UUID> getAccountUUID;


    protected int interval = 0;
    protected String unit = "";
    protected String allowedAccountType = "";
    protected final CoinPrice coinPrice = new CoinPrice();
    protected final Map<UUID, Boolean> subscribers = new HashMap<>();


    public DepositorBehaviour(SmartBlockEntity be, Supplier<UUID> getAccountUUID) {
        super(be);
        this.getAccountUUID = getAccountUUID;
    }

    @Override
    public BehaviourType<?> getType() { return TYPE; }

    @Nullable
    // Server only
    public ExtendedAccountData getExtendedAccount() {
        var uuid = getAccountUUID.get();
        if(uuid == null) return null;

        return NumismaticsSubscriptions.BANK.get(uuid);
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if(clientPacket){
            var extAcc = getExtendedAccount();
            if(extAcc == null){
                tag.putInt("Interval", 0);
                tag.putString("Unit", "");
                tag.putString("AllowedAccountType", "");
            }else{
                tag.putInt("Interval", extAcc.getInterval());
                tag.putString("Unit", extAcc.getUnit());
                tag.putString("AllowedAccountType", extAcc.getAllowedAccountType());

                extAcc.getCoinPrice().write(tag);

                ListTag subscribersTag = new ListTag();
                for (Map.Entry<UUID, Boolean> subscriber : extAcc.getSubscribers().entrySet()){
                    CompoundTag subscriberTag = new CompoundTag();
                    subscriberTag.putUUID("UUID", subscriber.getKey());
                    subscriberTag.putBoolean("Valid", subscriber.getValue());
                    subscribersTag.add(subscriberTag);
                }

                tag.put("Subscribers", subscribersTag);
            }
        }else{
            tag.putInt("Interval", interval);
            if(unit == null) unit = AllConstants.Time.HOURS;
            tag.putString("Unit", unit);

            if(allowedAccountType == null) allowedAccountType = AllConstants.AccountType.ALL;
            tag.putString("AllowedAccountType", allowedAccountType);

            coinPrice.write(tag);

            ListTag subscribersTag = new ListTag();
            for (Map.Entry<UUID, Boolean> subscriber : subscribers.entrySet()){
                CompoundTag subscriberTag = new CompoundTag();
                subscriberTag.putUUID("UUID", subscriber.getKey());
                subscriberTag.putBoolean("Valid", subscriber.getValue());
                subscribersTag.add(subscriberTag);
            }

            tag.put("Subscribers", subscribersTag);
        }
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        setInterval(tag.getInt("Interval"));
        setUnit(tag.getString("Unit"));
        setAllowedAccountType(tag.getString("AllowedAccountType"));

        this.coinPrice.read(tag);

        if(tag.contains("Subscribers")){
            subscribers.clear();
            var subscribersTag = tag.getList("Subscribers", Tag.TAG_COMPOUND);

            for (int i = 0, l = subscribersTag.size(); i < l; i++) {
                CompoundTag subscriberTag = subscribersTag.getCompound(i);
                subscribers.put(subscriberTag.getUUID("UUID"), subscriberTag.getBoolean("Valid"));
            }
        }
    }



    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAllowedAccountType() {
        return allowedAccountType;
    }

    public void setAllowedAccountType(String allowedAccountType) {
        this.allowedAccountType = allowedAccountType;
    }

    public int getTotalPrice() {
        return coinPrice.getTotalPrice();
    }

    public int[] getPrices(){
        return coinPrice.getPrices();
    }

    public int getPrice(Coin coin) {
        return coinPrice.getPrice(coin);
    }

    public void setPrice(Coin coin, int price) {
        coinPrice.setPrice(coin, price);
    }

    public Map<UUID, Boolean> getSubscribers() {
        return subscribers;
    }

    public void addSubscriber(UUID uuid) {
        subscribers.putIfAbsent(uuid, true);
    }

    public void setSubscriber(UUID uuid, boolean isValid) {
        subscribers.put(uuid, isValid);
    }

    public void removeSubscriber(UUID uuid) {
        subscribers.remove(uuid);
    }
}
