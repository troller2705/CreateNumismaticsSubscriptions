package com.troller2705.numismatics_subscriptions.content.backend;

import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.UUID;

public class ExtendedBankAccount extends BankAccount {

    private long interval;
    private String unit;
    private CoinPrice coinPrice;


    public ExtendedBankAccount(UUID id, Type type) { this(id, 0, type); }

    public ExtendedBankAccount(UUID id, int balance, Type type) { this(id, type, balance, false); }

    protected ExtendedBankAccount(UUID id, Type type, int balance, boolean clientSide) {
        super(id, type, balance, clientSide);
        interval = 20;
        unit = "Secs";
        coinPrice = new CoinPrice();
    }

    @Override
    public CompoundTag save(CompoundTag _tag){
        var tag = super.save(_tag);

        tag.putLong("Interval", this.interval);
        tag.putString("Unit", this.unit);
        this.coinPrice.write(tag);

        return tag;
    }

    public static BankAccount load(CompoundTag tag){

        if(!tag.contains("Interval")){
            return BankAccount.load(tag);
        }

        if(!tag.contains("Unit")){
            return BankAccount.load(tag);
        }

        ExtendedBankAccount account;

        if (tag.hasUUID("id")) {
            account = new ExtendedBankAccount(tag.getUUID("id"), Type.read(tag));
        } else {
            Numismatics.LOGGER.error("Account found without ID, deleting");
            return null;
        }
        account.setBalance(tag.getInt("balance"));
        ArrayList<UUID> trustList = new ArrayList<>();
        if (tag.contains("TrustList")) {
            trustList.addAll(NBTHelper.readCompoundList(
                    tag.getList("TrustList", Tag.TAG_COMPOUND),
                    t -> t.getUUID("UUID")
            ));

            account.updateTrustList(list -> {
                list.clear();
                list.addAll(trustList);
            });
        }
        if (account.type.hasLabel && tag.contains("Label", Tag.TAG_STRING))
            account.setLabel(tag.getString("Label"));

        if(tag.contains("Interval")){
            account.interval = tag.getLong("Interval");
        }

        if(tag.contains("Unit")){
            account.unit = tag.getString("Unit");
        }

        account.coinPrice.read(tag);

        return account;
    }


    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getUnit() { return unit; }

    public void setUnit(String unit) { this.unit = unit; }

    public CoinPrice getCoinPrice() {
        return coinPrice;
    }

}
