package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.troller2705.numismatics_subscriptions.AllConstants;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import com.troller2705.numismatics_subscriptions.content.backend.CoinPrice;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedAccountData;
import com.troller2705.numismatics_subscriptions.content.backend.SubscriptionBehavior;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.bank.blaze_banker.BankAccountBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtendedBankAccountBehaviour extends BankAccountBehaviour implements SubscriptionBehavior {

    public static final BehaviourType<ExtendedBankAccountBehaviour> TYPE = new BehaviourType<>();

    protected int interval = 0;
    protected String unit = "";
    protected String allowedAccountType = "";
    protected final CoinPrice coinPrice = new CoinPrice();
    protected final Map<UUID, Boolean> subscribers = new HashMap<>();


    public ExtendedBankAccountBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public ExtendedAccountData getExtendedAccount() {
        var uuid = getAccountUUID();


        // Make sure regular bankAccount exists
        var _bankAccount = getAccount();

        return NumismaticsSubscriptions.BANK.getOrCreate(uuid);
    }

    @Override
    public BehaviourType<?> getType() { return TYPE; }

    @Override
    public void destroy() {
        super.destroy();
        NumismaticsSubscriptions.BANK.extendedAccounts.remove(getAccountUUID());
        NumismaticsSubscriptions.BANK.markBankDirty();
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

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
