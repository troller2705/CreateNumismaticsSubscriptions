package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.troller2705.numismatics_subscriptions.SubscriptionPackets;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.bank.blaze_banker.BlazeBankerBlockEntity;
import dev.ithundxr.createnumismatics.registry.NumismaticsPackets;
import dev.ithundxr.createnumismatics.registry.packets.NumismaticsBlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class SubscriptionManagerEditPacket extends NumismaticsBlockEntityConfigurationPacket<SubscriptionManagerBlockEntity> {
    public static final StreamCodec<FriendlyByteBuf, SubscriptionManagerEditPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, i -> i.pos,
        CatnipStreamCodecBuilders.nullable(ByteBufCodecs.STRING_UTF8), i -> i.label,
        ByteBufCodecs.INT, i -> i.interval,
        CatnipStreamCodecBuilders.nullable(ByteBufCodecs.STRING_UTF8), i -> i.unit,
        CatnipStreamCodecBuilders.nullable(ByteBufCodecs.STRING_UTF8), i -> i.allowedAccountType,
        CatnipStreamCodecBuilders.array(ByteBufCodecs.INT, Integer.class), i -> i.coinPrices,
        SubscriptionManagerEditPacket::new
    );

    @Nullable
    private final String label;
    private final int interval;
    @Nullable
    private final String unit;
    @Nullable
    private final String allowedAccountType;
    private final Integer[] coinPrices;

    public SubscriptionManagerEditPacket(BlockPos pos, @Nullable String label, int interval, @Nullable String unit, @Nullable String allowedAccountType, Integer[] coinPrices) {
        super(pos);
        this.label = label;
        this.interval = interval;
        this.unit = unit;
        this.allowedAccountType = allowedAccountType;
        this.coinPrices = coinPrices;
    }

    @Override
    protected void applySettings(ServerPlayer player, SubscriptionManagerBlockEntity blazeBankerBlockEntity) {
        if (label != null)
            blazeBankerBlockEntity.setLabel(label);

        blazeBankerBlockEntity.setInterval(interval);

        if(unit != null)
            blazeBankerBlockEntity.setUnit(unit);

        if(allowedAccountType != null)
            blazeBankerBlockEntity.setAllowedAccountType(allowedAccountType);

        for (Coin coin : Coin.values()){
            blazeBankerBlockEntity.setPrice(coin, coinPrices[coin.ordinal()]);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return SubscriptionPackets.SUBSCRIPTION_MANAGER_EDIT;
    }
}
