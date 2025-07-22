package com.troller2705.numismatics_subscriptions.content.backend;

import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubscriptionsBankManager {
    private SubscriptionsBankSavedData savedData;
    public final Map<UUID, ExtendedBankAccount> extendedAccounts = new HashMap<>();

    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        loadBankData(server);
    }

    public void loadBankData(MinecraftServer server) {
        if (savedData != null)
            return;
        savedData = SubscriptionsBankSavedData.load(server);
        extendedAccounts.clear();
//        extendedAccounts.putAll(savedData.getAccounts());
    }


    public void markDirty() {
        if (savedData != null)
            savedData.setDirty();
    }

    public ExtendedBankAccount getOrCreate(UUID id) {
        return extendedAccounts.computeIfAbsent(id, this::createDefaultAccount);
    }


    public ExtendedBankAccount get(UUID uuid) {
        return extendedAccounts.get(uuid);
    }

    public void cleanUp() {
        extendedAccounts.clear();
    }

    public void markBankDirty()
    {
    }

    private ExtendedBankAccount createDefaultAccount(UUID id) {
        return new ExtendedBankAccount(id, BankAccount.Type.BLAZE_BANKER);
    }

}

