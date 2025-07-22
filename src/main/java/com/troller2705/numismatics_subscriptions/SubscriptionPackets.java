package com.troller2705.numismatics_subscriptions;

import com.troller2705.numismatics_subscriptions.content.subscription_manager.ExtendedBankAccountConfigurationPacket;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerEditPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum SubscriptionPackets implements BasePacketPayload.PacketTypeProvider
{
    SUBSCRIPTION_MANAGER_EDIT(SubscriptionManagerEditPacket.class, SubscriptionManagerEditPacket.STREAM_CODEC),
    EXTENDED_BANK_ACCOUNT_CONFIGURATION(ExtendedBankAccountConfigurationPacket.class, ExtendedBankAccountConfigurationPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> SubscriptionPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(NumismaticsSubscriptions.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(NumismaticsSubscriptions.MODID, 2); // increment version on changes
        for (SubscriptionPackets packet : SubscriptionPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
