package com.troller2705.numismatics_subscriptions.content.subscription_depositor;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ithundxr.createnumismatics.content.depositor.AbstractDepositorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class PatchedAbstractDepositorBlockEntity extends AbstractDepositorBlockEntity
{
    public PatchedAbstractDepositorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // NOOP
    }
}
