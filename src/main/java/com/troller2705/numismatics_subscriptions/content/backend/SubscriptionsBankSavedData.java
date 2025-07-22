package com.troller2705.numismatics_subscriptions.content.backend;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubscriptionsBankSavedData extends SavedData {
    private Map<UUID, ExtendedAccountData> accounts = new HashMap<>();

    public static SavedData.Factory<SubscriptionsBankSavedData> factory() {
        return new SavedData.Factory<>(SubscriptionsBankSavedData::new, SubscriptionsBankSavedData::load, null);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        tag.put("ExtendedAccounts", NBTHelper.writeCompoundList(accounts.values(), acc -> acc.save(new CompoundTag())));
        return tag;
    }

    private static SubscriptionsBankSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        var sd = new SubscriptionsBankSavedData();
        NBTHelper.iterateCompoundList(tag.getList("ExtendedAccounts", Tag.TAG_COMPOUND), compound -> {
            var account = ExtendedAccountData.load(compound);
            sd.accounts.put(account.id(), account);
        });
        return sd;
    }

    private SubscriptionsBankSavedData() {}

    public static SubscriptionsBankSavedData load(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(factory(), "numismatics_subscriptions_bank");
    }

//    public Map<? extends UUID, ? extends ExtendedBankAccount> getAccounts() {
//        return accounts;
//    }
}
