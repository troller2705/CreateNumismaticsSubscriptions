package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.troller2705.numismatics_subscriptions.SubscriptionPackets;
import com.troller2705.numismatics_subscriptions.content.backend.SubscriptionStatus;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.datafixers.util.Pair;

import java.util.List;

public class SyncSubscribersPacket implements ClientboundPacketPayload
{
    private final List<SubscriptionStatus> subscribers;

    public static final StreamCodec<FriendlyByteBuf, SyncSubscribersPacket> STREAM_CODEC = StreamCodec.composite(
            CatnipStreamCodecBuilders.list(SubscriptionStatus.STREAM_CODEC), i -> i.subscribers,
            SyncSubscribersPacket::new
    );

    public SyncSubscribersPacket(List<SubscriptionStatus> subscribers){
        this.subscribers = subscribers;
    }

    @Override
    public void handle(LocalPlayer player)
    {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().screen instanceof SubsListScreen subsList){
                subsList.receiveProfiles(subscribers);
            }
        });
    }

    @Override
    public PacketTypeProvider getTypeProvider()
    {
        return SubscriptionPackets.SYNC_GAME_PROFILES;
    }
}
