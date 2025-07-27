package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.mojang.authlib.GameProfile;
import com.troller2705.numismatics_subscriptions.SubscriptionPackets;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.datafixers.util.Pair;

import java.util.List;

public class SyncSubscribersPacket implements ClientboundPacketPayload
{
    private final List<Pair<GameProfile, Boolean>> profiles;

    public static final StreamCodec<FriendlyByteBuf, SyncSubscribersPacket> STREAM_CODEC = StreamCodec.composite(
            CatnipStreamCodecBuilders.list(CatnipStreamCodecBuilders.pair(ByteBufCodecs.GAME_PROFILE, ByteBufCodecs.BOOL)), i -> i.profiles,
            SyncSubscribersPacket::new
    );

    public SyncSubscribersPacket(List<Pair<GameProfile, Boolean>> profiles){
        this.profiles = profiles;
    }

    @Override
    public void handle(LocalPlayer player)
    {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().screen instanceof SubsListScreen subsList){
                subsList.receiveProfiles(profiles);
            }
        });
    }

    @Override
    public PacketTypeProvider getTypeProvider()
    {
        return SubscriptionPackets.SYNC_GAME_PROFILES;
    }
}
