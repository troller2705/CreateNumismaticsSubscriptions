package com.troller2705.numismatics_subscriptions.content.backend;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class ExtendedAccountData {

    public final UUID id;
    private final CoinPrice coinPrice;

    private long interval;
    private String unit;


    public ExtendedAccountData(UUID id) {
        this(id, 20L, "Secs", new CoinPrice());
    }

    protected ExtendedAccountData(UUID id, long interval, String unit, CoinPrice coinPrice){
        this.id = id;
        this.interval = interval;
        this.unit = unit;
        this.coinPrice = coinPrice;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public CoinPrice getCoinPrice() {
        return coinPrice;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putUUID("id", id);
        tag.putLong("Interval", interval);
        tag.putString("Unit", unit);
        coinPrice.write(tag);
        return tag;
    }

    public static ExtendedAccountData load(CompoundTag tag) {
        UUID id = tag.getUUID("id");
        long interval = tag.getLong("Interval");
        String unit = tag.getString("Unit");
        CoinPrice price = new CoinPrice();
        price.read(tag);
        return new ExtendedAccountData(id, interval, unit, price);
    }
}
