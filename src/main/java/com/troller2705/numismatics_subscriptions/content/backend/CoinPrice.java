package com.troller2705.numismatics_subscriptions.content.backend;

import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.EnumMap;

public class CoinPrice {

    private final EnumMap<Coin, Integer> prices = new EnumMap<>(Coin.class);

    public CoinPrice() {
        for (Coin coin : Coin.values()) {
            prices.put(coin, 0);
        }
    }

    public void reset(){
        this.prices.replaceAll(((coin, amount) -> 0));
    }

    public void setPrice(Coin coin, int amount){
        this.prices.put(coin, amount);
    }

    public int getPrice(Coin coin){
        return this.prices.getOrDefault(coin, 0);
    }

    public int[] getPrices(){
        int[] result = new int[Coin.values().length];
        for (Coin coin : Coin.values()){
            result[coin.ordinal()] = prices.get(coin);
        }
        return result;
    }

    public int getTotalPrice(){
        int result = 0;
        for (Coin coin : Coin.values()){
            result += coin.toSpurs(prices.getOrDefault(coin, 0));
        }
        return result;
    }

    public void read(CompoundTag tag){
        this.reset();
        if(tag.contains("Prices", CompoundTag.TAG_COMPOUND)){
            CompoundTag priceTag = tag.getCompound("Prices");
            for (Coin coin : Coin.values()){
                if(priceTag.contains(coin.getName(), Tag.TAG_INT)){
                    int count = priceTag.getInt(coin.getName());
                    setPrice(coin, count);
                }
            }
        }
    }

    public void write(CompoundTag tag){
        CompoundTag priceTag = new CompoundTag();
        for (Coin coin : Coin.values()){
            priceTag.putInt(coin.getName(), getPrice(coin));
        }
        tag.put("Prices", priceTag);
    }

}
