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


public class SubscriptionDepositorBlockEntity extends AbstractDepositorBlockEntity implements MenuProvider {

    private SliderStylePriceBehaviour price;

    public SubscriptionDepositorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        price = new SliderStylePriceBehaviour(this, this::addCoin, this::getCoinCount);
        behaviours.add(price);
    }

    public int getCoinCount(Coin coin) {
        return this.inventory.getDiscrete(coin);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.numismatics_subscriptions.subscription_depositor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        if (!isTrusted(player))
            return null;
        return new SubscriptionDepositorMenu(AllMenuTypes.SUBSCRIPTION_DEPOSITOR.get(), i, inventory, this);
    }

    public int getTotalPrice() {
        return price.getTotalPrice();
    }

    public int getPrice(Coin coin) {
        return price.getPrice(coin);
    }

    public void setPrice(Coin coin, int price) {
        this.price.setPrice(coin, price);
    }

    public void addCoins(int totalPrice) {
        MergingCoinBag coinBag = new MergingCoinBag(totalPrice);

        for (int i = Coin.values().length - 1; i >= 0; i--) {
            Coin coin = Coin.values()[i];
            int count = coinBag.get(coin).getFirst();
            if (count > 0) {
                coinBag.subtract(coin, count);
                addCoin(coin, count);
            }
        }
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Couple<Integer> cogsAndSpurs = Coin.COG.convert(price.getTotalPrice());
        int cogs = cogsAndSpurs.getFirst();
        int spurs = cogsAndSpurs.getSecond();
        MutableComponent balanceLabel = Component.translatable("block.numismatics_subscriptions.subscription_depositor.tooltip.price",
                TextUtils.formatInt(cogs), Coin.COG.getName(cogs), spurs, "500", "Secs");
        Lang.builder(NumismaticsSubscriptions.MODID)
                .add(balanceLabel.withStyle(Coin.closest(price.getTotalPrice()).rarity.color()))
                .forGoggles(tooltip);

        for (MutableComponent component : price.getCondensedPriceBreakdown()) {
            Lang.builder(NumismaticsSubscriptions.MODID)
                    .add(component)
                    .forGoggles(tooltip);
        }
        return true;
    }

    @Override
    public void openTrustListMenu(ServerPlayer player) {
        //TrustListMenu.openMenu(this, player, AllMenuTypes.SUBSCRIPTION_DEPOSITOR.asStack());
    }
}
