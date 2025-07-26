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

import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.AllMenuTypes;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedAccountData;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedBankAccountBehaviour;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.backend.behaviours.SliderStylePriceBehaviour;
import dev.ithundxr.createnumismatics.content.coins.MergingCoinBag;
import dev.ithundxr.createnumismatics.content.depositor.AbstractDepositorBlockEntity;
import dev.ithundxr.createnumismatics.util.TextUtils;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class SubscriptionDepositorBlockEntity extends AbstractDepositorBlockEntity implements MenuProvider {

    // only available on client
    private int clientsideBalance = 0;

    // only available on server
    private int serversideBalance = 0;

    protected ExtendedBankAccountBehaviour bankAccountBehaviour;

    @Nullable
    protected UUID owner;

    public SubscriptionDepositorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ExtendedAccountData getExtendedAccount(){
        if (this.isRemoved()) {
            Numismatics.LOGGER.error("Tried to get extended account from removed banker!");
            return null;
        }
        if (bankAccountBehaviour == null) {
            return null;
        }
        return bankAccountBehaviour.getExtendedAccount();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public @NotNull Component getDisplayName() {
        if (bankAccountBehaviour.hasAccount())
        {
            return Component.literal("Account");
        }
        else
        {
            return Component.translatable("block.numismatics_subscriptions.subscription_depositor");
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        if (!isTrusted(player))
            return null;
        return new SubscriptionDepositorMenu(AllMenuTypes.SUBSCRIPTION_DEPOSITOR.get(), i, inventory, this);
    }

    public int getTotalPrice() {
        return bankAccountBehaviour.getTotalPrice();
    }

    public int getInterval(){ return bankAccountBehaviour.getInterval(); }

    public String getUnit(){ return bankAccountBehaviour.getUnit(); }

    public String getAllowedAccountType(){ return bankAccountBehaviour.getAllowedAccountType(); }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Couple<Integer> cogsAndSpurs = Coin.COG.convert(getTotalPrice());
        int cogs = cogsAndSpurs.getFirst();
        int spurs = cogsAndSpurs.getSecond();
        MutableComponent balanceLabel = Component.translatable("block.numismatics_subscriptions.subscription_depositor.tooltip.price",
                TextUtils.formatInt(cogs), Coin.COG.getName(cogs), spurs, getInterval(), getUnit());
        Lang.builder(NumismaticsSubscriptions.MODID)
                .add(balanceLabel.withStyle(Coin.closest(getTotalPrice()).rarity.color()))
                .forGoggles(tooltip);

        return true;
    }


    @Override
    public void openTrustListMenu(ServerPlayer player) {}
}
