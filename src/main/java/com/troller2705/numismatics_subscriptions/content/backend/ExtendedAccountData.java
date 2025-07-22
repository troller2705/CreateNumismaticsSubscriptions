package com.troller2705.numismatics_subscriptions.content.backend;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public record ExtendedAccountData(UUID id, long interval, String unit, CoinPrice coinPrice) {

    public ExtendedAccountData(UUID id) {
        this(id, 20L, "Secs", new CoinPrice());
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
