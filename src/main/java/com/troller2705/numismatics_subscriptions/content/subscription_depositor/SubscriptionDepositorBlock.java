/*
 * Numismatics
 * Copyright (c) 2023-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.troller2705.numismatics_subscriptions.content.subscription_depositor;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.AllBlockEntities;
import com.troller2705.numismatics_subscriptions.AllConstants;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.behaviours.SliderStylePriceBehaviour;
import dev.ithundxr.createnumismatics.content.bank.CardItem;
import dev.ithundxr.createnumismatics.content.depositor.AbstractDepositorBlock;
import dev.ithundxr.createnumismatics.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SubscriptionDepositorBlock extends AbstractDepositorBlock<SubscriptionDepositorBlockEntity>
{
    public SubscriptionDepositorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<SubscriptionDepositorBlockEntity> getBlockEntityClass() {
        return SubscriptionDepositorBlockEntity.class;
    }

    @Override
    public BlockEntityType<SubscriptionDepositorBlockEntity> getBlockEntityType() {
        return AllBlockEntities.SUBSCRIPTION_DEPOSITOR.get();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection().getAxis().isVertical()) {
            if (level.isClientSide)
                return ItemInteractionResult.SUCCESS;
            if (isTrusted(player, level, pos)) {
                withBlockEntityDo(level, pos,
                        be -> Utils.openScreen((ServerPlayer) player, be, be::sendToMenu));
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (state.getValue(HORIZONTAL_FACING) != hitResult.getDirection())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        var item = player.getItemInHand(hand);

        // Check is CardItem
        if(item == null || item.isEmpty() || !(item.getItem() instanceof CardItem)){

            player.displayClientMessage(Component.literal("Need to use a card").withStyle(ChatFormatting.DARK_RED), true);
            level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.BLOCKS, 0.5f, 1.0f);

            return ItemInteractionResult.CONSUME;
        }

        // Check isBound
        if(!CardItem.isBound(item)){
            player.displayClientMessage(Component.literal("Card is not bound").withStyle(ChatFormatting.DARK_RED), true);
            level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.BLOCKS, 0.5f, 1.0f);

            return ItemInteractionResult.CONSUME;
        }

        var cardUUID = CardItem.get(item);
        var account = Numismatics.BANK.getAccount(cardUUID);

        var be = (SubscriptionDepositorBlockEntity)level.getBlockEntity(pos);

        // Check if accountType is allowed
        switch (be.getAllowedAccountType()){
            case AllConstants.AccountType.BANK:

                if(account.type != BankAccount.Type.BLAZE_BANKER){
                    player.displayClientMessage(Component.literal("Only Banker cards allowed").withStyle(ChatFormatting.DARK_RED), true);
                    level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.BLOCKS, 0.5f, 1.0f);
                }

                return ItemInteractionResult.CONSUME;
            case AllConstants.AccountType.PRIVATE:

                if(account.type != BankAccount.Type.PLAYER){
                    player.displayClientMessage(Component.literal("Only Personal cards allowed").withStyle(ChatFormatting.DARK_RED), true);
                    level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.BLOCKS, 0.5f, 1.0f);
                }

                return ItemInteractionResult.CONSUME;
            default: break;
        }

        //TODO: Check blockstates
        //TODO: Check if player is subscriber and wants to unsubscribe

        // Check if player can afford
        if(account.getBalance() < be.getTotalPrice()){
            player.displayClientMessage(Component.translatable("gui.numismatics.vendor.insufficient_funds").withStyle(ChatFormatting.DARK_RED), true);
            level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.BLOCKS, 0.5f, 1.0f);

            return ItemInteractionResult.CONSUME;
        }

        //TODO: Subscribe



        return ItemInteractionResult.CONSUME;
    }
}