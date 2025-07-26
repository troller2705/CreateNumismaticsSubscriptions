package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.SubscriptionPackets;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedBankAccountBehaviour;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.registry.packets.BlockEntityBehaviourConfigurationPacket;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

@Deprecated()
public class ExtendedBankAccountConfigurationPacket extends BlockEntityBehaviourConfigurationPacket<ExtendedBankAccountBehaviour> {

    public static final StreamCodec<FriendlyByteBuf, ExtendedBankAccountConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, i -> i.pos,
            ByteBufCodecs.INT, i -> i.interval,
            CatnipStreamCodecBuilders.nullable(ByteBufCodecs.STRING_UTF8), i -> i.unit,
            CatnipStreamCodecBuilders.nullable(ByteBufCodecs.STRING_UTF8), i -> i.allowedAccountType,
            CatnipStreamCodecBuilders.array(ByteBufCodecs.INT, Integer.class), i -> i.coinPrices,
            ExtendedBankAccountConfigurationPacket::new
    );

    private final int interval;
    private final String unit;
    private final String allowedAccountType;
    private final Integer[] coinPrices;


    public ExtendedBankAccountConfigurationPacket(BlockPos pos, int interval, String unit, String allowedAccountType, Integer[] coinPrices) {
        super(pos);
        this.interval = interval;
        this.unit = unit;
        this.allowedAccountType = allowedAccountType;
        this.coinPrices = coinPrices;
    }

    public ExtendedBankAccountConfigurationPacket(SyncedBlockEntity be) {
        super(be.getBlockPos());
        this.coinPrices = new Integer[Coin.values().length];

        ExtendedBankAccountBehaviour extBankBehaviour = BlockEntityBehaviour.get(be, getType());
        this.interval = extBankBehaviour.getInterval();
        this.unit = extBankBehaviour.getUnit();
        this.allowedAccountType = extBankBehaviour.getAllowedAccountType();
        for (Coin coin : Coin.values()) {
            this.coinPrices[coin.ordinal()] = extBankBehaviour.getPrice(coin);
        }
    }

    @Override
    protected BehaviourType<ExtendedBankAccountBehaviour> getType() {
        return ExtendedBankAccountBehaviour.TYPE;
    }

    @Override
    protected void applySettings(ServerPlayer player, ExtendedBankAccountBehaviour behaviour) {
        behaviour.setInterval(interval);
        behaviour.setUnit(unit);
        behaviour.setAllowedAccountType(allowedAccountType);
        for (Coin coin : Coin.values()) {
            behaviour.setPrice(coin, coinPrices[coin.ordinal()]);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() { return SubscriptionPackets.EXTENDED_BANK_ACCOUNT_CONFIGURATION; }
}
