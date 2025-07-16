package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.simibubi.create.AllBlockEntityTypes;
import com.troller2705.numismatics_subscriptions.AllBlocks;
import dev.ithundxr.createnumismatics.registry.NumismaticsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SubscriptionGuideItem extends Item {

    public SubscriptionGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(clickedPos);
        if (blockEntity != null) {
            if (AllBlockEntityTypes.HEATER.is(blockEntity)) {
                BlockState state = AllBlocks.SUBSCRIPTION_MANAGER.getDefaultState();
                if (level.setBlockAndUpdate(clickedPos, state)) {
                    state.getBlock().setPlacedBy(level, clickedPos, state, context.getPlayer(), context.getItemInHand());
                }
                context.getItemInHand().shrink(1);
                level.playSound(null, clickedPos, SoundEvents.ARROW_HIT_PLAYER, SoundSource.BLOCKS, 0.5f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}
