package com.troller2705.numismatics_subscriptions.mixin;

import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(BankSavedData.class)
public abstract class BankSavedDataMixin {

    @SuppressWarnings("DataFlowIssue")
    @Invoker("<init>")
    public static BankSavedData callConstructor() {
        return null;
    }

    @SuppressWarnings("DataFlowIssue")
    @Accessor("accounts")
    public abstract Map<UUID, BankAccount> getAccounts();

    @SuppressWarnings("DataFlowIssue")
    @Accessor("accounts")
    public abstract void setAccounts(Map<UUID, BankAccount> accounts);


//    @Inject(
//            method = "load(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Ldev/ithundxr/createnumismatics/content/backend/BankSavedData;",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private static void injectCustomLoad(CompoundTag tag, HolderLookup.Provider provider, CallbackInfoReturnable<BankSavedData> cir) {
//        BankSavedData custom = callConstructor();
//        BankSavedDataMixin accessor = (BankSavedDataMixin) (Object) custom;
//
//        Map<UUID, BankAccount> accounts = new HashMap<>();
//
//        NBTHelper.iterateCompoundList(tag.getList("Accounts", Tag.TAG_COMPOUND), c -> {
//            BankAccount account = ExtendedBankAccount.load(c);
//            if (account != null)
//                accounts.put(account.id, account);
//        });
//
//        accessor.setAccounts(accounts);
//
//        cir.setReturnValue(custom);
//    }
//
//    @Inject(
//            method = "save(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void injectCustomSave(CompoundTag tag, HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
//
//        tag.put("Accounts", NBTHelper.writeCompoundList(Numismatics.BANK.accounts.values(), t -> t.save(new CompoundTag())));
//
//        cir.setReturnValue(tag);
//    }

}

