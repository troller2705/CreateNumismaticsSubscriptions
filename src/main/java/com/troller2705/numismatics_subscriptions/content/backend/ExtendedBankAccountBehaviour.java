package com.troller2705.numismatics_subscriptions.content.backend;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.bank.blaze_banker.BankAccountBehaviour;

public class ExtendedBankAccountBehaviour  extends BankAccountBehaviour {
    public ExtendedBankAccountBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public ExtendedBankAccount getAccount() {
        var uuid = getAccountUUID();
        if(NumismaticsSubscriptions.BANK.extendedAccounts.get(uuid) == null){
            var account = new ExtendedBankAccount(uuid, BankAccount.Type.BLAZE_BANKER);
            NumismaticsSubscriptions.BANK.extendedAccounts.put(uuid, account);
            NumismaticsSubscriptions.BANK.markBankDirty();
            return account;
        }

        return NumismaticsSubscriptions.BANK.extendedAccounts.get(uuid);
    }
}
