package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.troller2705.numismatics_subscriptions.SubscriptionPackets;
import dev.ithundxr.createnumismatics.registry.packets.NumismaticsBlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class OpenSubsListPacket<BE extends SyncedBlockEntity & SubsListHolder> extends NumismaticsBlockEntityConfigurationPacket<BE>
{
    @SuppressWarnings("rawtypes")
    public static final StreamCodec<ByteBuf, OpenSubsListPacket> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(OpenSubsListPacket::new, i -> i.pos);

    public OpenSubsListPacket(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void applySettings(ServerPlayer player, BE be) {
        be.openSubsListMenu(player);
    }

    @Override
    protected boolean causeUpdate() {
        return false;
    }

    @Override
    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return SubscriptionPackets.OPEN_SUBS_LIST;
    }
}
