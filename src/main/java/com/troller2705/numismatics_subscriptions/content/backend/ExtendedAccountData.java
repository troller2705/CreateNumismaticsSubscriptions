package com.troller2705.numismatics_subscriptions.content.backend;

import com.troller2705.numismatics_subscriptions.AllConstants;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class ExtendedAccountData {

    public final UUID id;
    private final CoinPrice coinPrice;

    private int interval;
    private String unit;
    private String allowedAccountType;


    public ExtendedAccountData(UUID id) {
        this(id, 20, AllConstants.Time.SECONDS, AllConstants.AccountType.ALL, new CoinPrice());
    }

    protected ExtendedAccountData(UUID id, int interval, String unit, String allowedAccountType, CoinPrice coinPrice){
        this.id = id;
        this.interval = interval;
        this.unit = unit;
        this.allowedAccountType = allowedAccountType;
        this.coinPrice = coinPrice;
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

    public CoinPrice getCoinPrice() {
        return coinPrice;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putUUID("id", id);
        tag.putInt("Interval", interval);
        tag.putString("Unit", unit);
        tag.putString("AllowedAccountType", allowedAccountType);
        coinPrice.write(tag);
        return tag;
    }

    public static ExtendedAccountData load(CompoundTag tag) {
        UUID id = tag.getUUID("id");
        int interval = tag.getInt("Interval");
        String unit = tag.getString("Unit");
        String allowedAccountType = tag.getString("AllowedAccountType");
        CoinPrice price = new CoinPrice();
        price.read(tag);
        return new ExtendedAccountData(id, interval, unit, allowedAccountType, price);
    }
}
