package com.troller2705.numismatics_subscriptions.content.backend;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record SubscriptionStatus(UUID id, String name, Boolean isSubscribed) {

    public static final StreamCodec<FriendlyByteBuf, SubscriptionStatus> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, i -> i.id,
            CatnipStreamCodecBuilders.nullable(ByteBufCodecs.STRING_UTF8), i -> i.name,
            ByteBufCodecs.BOOL, i -> i.isSubscribed,
            SubscriptionStatus::new
    );

}
