package com.troller2705.numismatics_subscriptions.content.subscription_depositor;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.AllConstants;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedAccountData;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SubscriptionBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<SubscriptionBehaviour> TYPE = new BehaviourType<>();

    protected final Supplier<UUID> getAccountUUID;

    public SubscriptionBehaviour(SmartBlockEntity be, Supplier<UUID> getAccountUUID) {
        super(be);
        this.getAccountUUID = getAccountUUID;
    }

    @Override
    public BehaviourType<?> getType() { return TYPE; }

    @Nullable
    public ExtendedAccountData getExtendedAccount() {
        var uuid = getAccountUUID.get();
        if(uuid == null) return null;

        return NumismaticsSubscriptions.BANK.get(uuid);
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
    }


    public int getTotalPrice() {
        var extAcc = getExtendedAccount();
        if(extAcc == null) return 0;
        var coinPrice = extAcc.getCoinPrice();
        if(coinPrice == null) return 0;

        return coinPrice.getTotalPrice();
    }

    public Integer[] getPrices(){
        Integer[] result = new Integer[Coin.values().length];

        var extAcc = getExtendedAccount();
        if(extAcc == null) return result;
        var coinPrice = extAcc.getCoinPrice();
        if(coinPrice == null) return result;

        for (Coin coin : Coin.values()){
            result[coin.ordinal()] = coinPrice.getPrice(coin);
        }
        return result;
    }

    public int getPrice(Coin coin) {
        var extAcc = getExtendedAccount();
        if(extAcc == null) return 0;
        var coinPrice = extAcc.getCoinPrice();
        if(coinPrice == null) return 0;

        return coinPrice.getPrice(coin);
    }

    public int getInterval() {
        var extAcc = getExtendedAccount();
        if(extAcc == null) return -1;

        return extAcc.getInterval();
    }


    public String getUnit() {
        var extAcc = getExtendedAccount();
        if(extAcc == null) return AllConstants.Time.HOURS;

        return extAcc.getUnit();
    }


    public String getAllowedAccountType() {
        var extAcc = getExtendedAccount();
        if(extAcc == null) return AllConstants.AccountType.ALL;

        return extAcc.getAllowedAccountType();
    }
}
