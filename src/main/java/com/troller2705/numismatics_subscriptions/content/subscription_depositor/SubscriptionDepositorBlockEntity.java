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

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.AllMenuTypes;
import com.troller2705.numismatics_subscriptions.NumismaticsSubscriptions;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedAccountData;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.bank.CardItem;
import dev.ithundxr.createnumismatics.content.depositor.AbstractDepositorBlockEntity;
import dev.ithundxr.createnumismatics.util.TextUtils;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.platform.Env;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class SubscriptionDepositorBlockEntity extends AbstractDepositorBlockEntity implements MenuProvider {

    private DepositorBehaviour subscription;

    @Nullable
    protected UUID owner;

    public SubscriptionDepositorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        subscription = new DepositorBehaviour(this, this::getCardId);
        behaviours.add(subscription);
    }

    @Override
    public @NotNull Component getDisplayName() {
        if (!cardContainer.getItem(0).isEmpty())
        {
            ItemStack card = cardContainer.getItem(0);
            String name;
            if (Env.CLIENT.isCurrent())
            {
                 name = CardItem.getPlayerName(card);
            }
            else
            {
                 name = Numismatics.BANK.getAccount(CardItem.get(card)).getLabel();
            }
//            assert name != null;
            return Component.literal(name);
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

    @Override
    public void lazyTick() {
        super.lazyTick();

        if (level == null || level.isClientSide)
            return;

        var extAcc = getExtendedAccount();
        if(extAcc != null){
            boolean shouldUpdate = false;
            shouldUpdate |= getInterval() != extAcc.getInterval() || getInterval() == 0;
            shouldUpdate |= !Objects.equals(getUnit(), extAcc.getUnit());
            shouldUpdate |= !Objects.equals(getAllowedAccountType(), extAcc.getAllowedAccountType());
            shouldUpdate |= getTotalPrice() != extAcc.getCoinPrice().getTotalPrice();
            shouldUpdate |= getSubscribers().size() != extAcc.getSubscribers().size();

            if(shouldUpdate)
                notifyUpdate();
        }
    }

    public ExtendedAccountData getExtendedAccount(){
        if(subscription == null) return null;

        return subscription.getExtendedAccount();
    }



    public int getInterval(){ return subscription.getInterval(); }

    public String getUnit(){ return subscription.getUnit(); }

    public String getAllowedAccountType(){ return subscription.getAllowedAccountType(); }

    public int getTotalPrice() { return subscription.getTotalPrice(); }

    public int[] getPrices() { return subscription.getPrices(); }

    public int getPrice(Coin coin) { return subscription.getPrice(coin); }

    public Map<UUID, Boolean> getSubscribers() { return subscription.getSubscribers(); }

    public void addSubscriber(UUID subscriber){
        if (level != null && !level.isClientSide) {
            getExtendedAccount().addSubscriber(subscriber);
        }

        subscription.addSubscriber(subscriber);

        notifyUpdate();
    }

    public void removeSubscriber(UUID subscriber){
        if (level != null && !level.isClientSide) {
            getExtendedAccount().removeSubscriber(subscriber);
        }

        subscription.removeSubscriber(subscriber);

        notifyUpdate();
    }



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
