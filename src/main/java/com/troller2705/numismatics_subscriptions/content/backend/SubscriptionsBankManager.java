package com.troller2705.numismatics_subscriptions.content.backend;

import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.util.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubscriptionsBankManager {
    private SubscriptionsBankSavedData savedData;
    public Map<UUID, ExtendedAccountData> extendedAccounts = new HashMap<>();

    public static final SubscriptionProcessor SUBSCRIPTION_PROCESSOR = new SubscriptionProcessor();

    public SubscriptionsBankManager() {
        cleanUp();
    }

    private void warnIfClient() {
        if (Thread.currentThread().getName().equals("Render thread")) {
            long start = System.currentTimeMillis();
            Numismatics.LOGGER.error("Bank manager should not be accessed on the client"); // set breakpoint here when developing
            if (Utils.isDevEnv()) {
                long end = System.currentTimeMillis();
                if (end - start < 50) { // crash if breakpoint wasn't set
                    throw new RuntimeException("Illegal bank access performed on client, please set a breakpoint above");
                }
            } else {
                Numismatics.LOGGER.error("Stacktrace: ", new RuntimeException("Illegal bank access performed on client"));
            }
        }
    }


    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        cleanUp();
        savedData = null;
        loadBankData(server);
    }

    private void onBankAccountDirty(){
        markBankDirty();
    }

    public void loadBankData(MinecraftServer server) {
        if (savedData != null)
            return;
        savedData = SubscriptionsBankSavedData.load(server);
        savedData.getAccounts().forEach((uuid, extendedAccountData) -> {
            extendedAccountData.setOnDirty(this::onBankAccountDirty);
        });
        extendedAccounts = savedData.getAccounts();
        SUBSCRIPTION_PROCESSOR.initialize();
    }

    public void cleanUp() {
        extendedAccounts.clear();
    }

    public void markBankDirty()
    {
        if(savedData != null)
            savedData.setDirty();
    }

    public ExtendedAccountData getOrCreate(UUID id) {
        warnIfClient();
        if(extendedAccounts.containsKey(id)){
            return extendedAccounts.get(id);
        }else{
            var account = new ExtendedAccountData(id);
            account.setOnDirty(this::onBankAccountDirty);
            extendedAccounts.put(id, account);
            markBankDirty();
            return account;
        }
    }


    public ExtendedAccountData get(UUID uuid) {
        return extendedAccounts.get(uuid);
    }

}

