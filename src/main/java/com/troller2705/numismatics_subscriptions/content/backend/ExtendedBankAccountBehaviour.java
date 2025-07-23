package com.troller2705.numismatics_subscriptions.content.backend;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.troller2705.numismatics_subscriptions.AllConstants;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.bank.blaze_banker.BankAccountBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.EnumMap;
import java.util.Map;

public class ExtendedBankAccountBehaviour extends BankAccountBehaviour {

    public static final BehaviourType<ExtendedBankAccountBehaviour> TYPE = new BehaviourType<>();

    protected int interval;
    protected String unit;
    protected String allowedAccountType;
    protected final EnumMap<Coin, Integer> prices = new EnumMap<>(Coin.class);


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

        CompoundTag priceTag = new CompoundTag();
        for (Coin coin : Coin.values()) {
            priceTag.putInt(coin.getName(), getPrice(coin));
        }
        tag.put("Prices", priceTag);
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        this.interval = tag.getInt("Interval");
        this.unit = tag.getString("Unit");
        this.allowedAccountType = tag.getString("AllowedAccountType");

        this.prices.clear();
        if (tag.contains("Prices", Tag.TAG_COMPOUND)) {
            CompoundTag priceTag = tag.getCompound("Prices");
            for (Coin coin : Coin.values()) {
                if (priceTag.contains(coin.getName(), Tag.TAG_INT)) {
                    int count = priceTag.getInt(coin.getName());
                    if (count > 0)
                        setPrice(coin, count);
                }
            }
        }
        calculateTotalPrice();
    }

    private int totalPrice = 0;

    private void calculateTotalPrice() {
        totalPrice = 0;
        for (Map.Entry<Coin, Integer> entry : prices.entrySet()) {
            totalPrice += entry.getKey().toSpurs(entry.getValue());
        }
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public Integer[] getPrices(){
        Integer[] result = new Integer[Coin.values().length];
        for (Coin coin : Coin.values()){
            result[coin.ordinal()] = prices.get(coin);
        }
        return result;
    }

    public int getPrice(Coin coin) {
        return prices.getOrDefault(coin, 0);
    }

    public void setPrice(Coin coin, int price) {
        this.prices.put(coin, price);
        calculateTotalPrice();
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
}
