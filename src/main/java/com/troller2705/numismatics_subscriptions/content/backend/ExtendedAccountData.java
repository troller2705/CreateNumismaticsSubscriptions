package com.troller2705.numismatics_subscriptions.content.backend;

import com.troller2705.numismatics_subscriptions.AllConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtendedAccountData {

    @Nullable
    private Runnable onDirty;

    public final UUID id;
    private final CoinPrice coinPrice;

    private int interval;
    private String unit;
    private String allowedAccountType;

    private final Map<UUID, Boolean> subscribers = new HashMap<>();


    public ExtendedAccountData(UUID id) {
        this(id, 20, AllConstants.Time.SECONDS, AllConstants.AccountType.ALL, new CoinPrice(), new HashMap<>());
    }

    protected ExtendedAccountData(UUID id, int interval, String unit, String allowedAccountType, CoinPrice coinPrice, Map<UUID, Boolean> subscribers)
    {
        this.id = id;
        this.interval = interval;
        this.unit = unit;
        this.allowedAccountType = allowedAccountType;
        this.coinPrice = coinPrice;
        this.subscribers.putAll(subscribers);
    }

    public void setOnDirty(@NotNull Runnable onDirty){
        this.onDirty = onDirty;
    }


    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        if(onDirty != null) onDirty.run();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        if(onDirty != null) onDirty.run();
    }

    public String getAllowedAccountType() {
        return allowedAccountType;
    }

    public void setAllowedAccountType(String allowedAccountType) {
        this.allowedAccountType = allowedAccountType;
        if(onDirty != null) onDirty.run();
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

        ListTag list = new ListTag();
        for (Map.Entry<UUID, Boolean> entry : subscribers.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putUUID("UUID", entry.getKey());
            t.putBoolean("Valid", entry.getValue());
            list.add(t);
        }
        tag.put("Subscribers", list);

        return tag;
    }

    public static ExtendedAccountData load(CompoundTag tag) {
        UUID id = tag.getUUID("id");
        int interval = tag.getInt("Interval");
        String unit = tag.getString("Unit");
        String allowedAccountType = tag.getString("AllowedAccountType");
        CoinPrice price = new CoinPrice();
        price.read(tag);

        ListTag list = tag.getList("Subscribers", Tag.TAG_COMPOUND);
        HashMap<UUID, Boolean> subscribers_temp = new HashMap<>();
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            UUID uuid = entry.getUUID("UUID");
            boolean valid = entry.getBoolean("Valid");
            subscribers_temp.put(uuid, valid);
        }

        return new ExtendedAccountData(id, interval, unit, allowedAccountType, price, subscribers_temp);
    }

    public Map<UUID, Boolean> getAll() {
        return subscribers;
    }
}
