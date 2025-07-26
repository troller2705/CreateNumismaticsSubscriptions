package com.troller2705.numismatics_subscriptions.content.backend;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubscriptionsBankData extends SavedData
{
    private final Map<UUID, ExtendedAccountData> extendedAccounts = new HashMap<>();
    private final Map<UUID, Boolean> subscribers = new HashMap<>();

    public static SubscriptionsBankData load(CompoundTag tag, HolderLookup.Provider provider) {
        var data = new SubscriptionsBankData();
        ListTag accounts = tag.getList("ExtendedAccounts", Tag.TAG_COMPOUND);
        for (Tag t : accounts) {
            CompoundTag accTag = (CompoundTag) t;
            ExtendedAccountData account = ExtendedAccountData.load(accTag);
            data.extendedAccounts.put(account.id, account);
        }
        ListTag list = tag.getList("Subscribers", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            UUID uuid = entry.getUUID("UUID");
            boolean valid = entry.getBoolean("Valid");
            data.subscribers.put(uuid, valid);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag accounts = new ListTag();
        for (ExtendedAccountData account : extendedAccounts.values()) {
            accounts.add(account.save(new CompoundTag()));
        }
        tag.put("ExtendedAccounts", accounts);

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

    public static SavedData.Factory<SubscriptionsBankData> factory() {
        return new SavedData.Factory<>(SubscriptionsBankData::new, SubscriptionsBankData::load, null);
    }

    public static SubscriptionsBankData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(factory(), "numismatics_subscriptions_bank");
    }

    public Map<UUID, Boolean> getAll() {
        return subscribers;
    }

    public ExtendedAccountData getOrCreate(UUID id) {
        return extendedAccounts.computeIfAbsent(id, ExtendedAccountData::new);
    }
}
