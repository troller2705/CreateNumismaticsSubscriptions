package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.troller2705.numismatics_subscriptions.AllBlockEntities;
import dev.ithundxr.createnumismatics.base.block.ConditionalBreak;
import dev.ithundxr.createnumismatics.base.block.NotifyFailedBreak;
import dev.ithundxr.createnumismatics.content.backend.TrustedBlock;
import dev.ithundxr.createnumismatics.registry.NumismaticsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubscriptionManagerBlock extends Block implements IWrenchable, IBE<SubscriptionManagerBlockEntity>, TrustedBlock, NotifyFailedBreak, ConditionalBreak {
    public SubscriptionManagerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<SubscriptionManagerBlockEntity> getBlockEntityClass() {
        return SubscriptionManagerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SubscriptionManagerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.SUBSCRIPTION_MANAGER.get();
    }

    @Override
    public void notifyFailedBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof SubscriptionManagerBlockEntity blazeBankerBE) {
            blazeBankerBE.notifyDelayedDataSync();
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    // this is an additional check ON TOP OF the trust check
    @Override
    public boolean mayBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        return mayBreak(level, pos, state, player, false);
    }

    private boolean mayBreak(LevelAccessor level, BlockPos pos, BlockState state, Player player, boolean forDestroyProgress) {
        if (level.isClientSide() && !forDestroyProgress)
            return true;

        if (!(level.getBlockEntity(pos) instanceof SubscriptionManagerBlockEntity blazeBankerBE))
            return true;

        if (level.isClientSide())
            return blazeBankerBE.getClientsideBalance() == 0;

        return !blazeBankerBE.hasAccount() || blazeBankerBE.getAccount().getBalance() == 0;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        withBlockEntityDo(level, pos, be -> {
            if (be.owner == null) {
                be.owner = player.getUUID();
                be.notifyUpdate();
            }
        });

        if (isTrusted(player, level, pos)) {
            withBlockEntityDo(level, pos,
                    be -> be.openTrustListMenu((ServerPlayer) player));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof SubscriptionManagerBlockEntity blazeBankerBE) {
            blazeBankerBE.owner = player.getUUID();
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (!isTrusted(context.getPlayer(), context.getLevel(), context.getClickedPos()))
            return InteractionResult.FAIL;
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return AllShapes.HEATER_BLOCK_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                                 @NotNull CollisionContext context) {
        if (context == CollisionContext.empty())
            return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
        return getShape(state, level, pos, context);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public void animateTick(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, RandomSource random) {
        if (random.nextInt(10) != 0)
            return;
        world.playLocalSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F,
                (float) pos.getZ() + 0.5F, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        if (!isTrusted(player, level, pos) || !mayBreak(player.level(), pos, state, player, true)) {
            return 0.0f;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }
}
