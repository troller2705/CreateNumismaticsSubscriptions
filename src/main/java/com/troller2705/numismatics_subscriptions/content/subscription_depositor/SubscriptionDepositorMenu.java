package com.troller2705.numismatics_subscriptions.content.subscription_depositor;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.bank.CardSlot;
import dev.ithundxr.createnumismatics.content.coins.CoinItem;
import dev.ithundxr.createnumismatics.registry.NumismaticsTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SubscriptionDepositorMenu extends MenuBase<SubscriptionDepositorBlockEntity> {
    public static final int COIN_SLOTS = Coin.values().length;
    public static final int CARD_SLOT_INDEX = COIN_SLOTS;
    public static final int PLAYER_INV_START_INDEX = CARD_SLOT_INDEX + 1;
    public static final int PLAYER_HOTBAR_END_INDEX = PLAYER_INV_START_INDEX + 9;
    public static final int PLAYER_INV_END_INDEX = PLAYER_INV_START_INDEX + 36;
    public SubscriptionDepositorMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public SubscriptionDepositorMenu(MenuType<?> type, int id, Inventory inv, SubscriptionDepositorBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    @Override
    protected SubscriptionDepositorBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof SubscriptionDepositorBlockEntity subscriptionDepositorBE) {
            subscriptionDepositorBE.readClient(Objects.requireNonNull(extraData.readNbt()), extraData.registryAccess());
            return subscriptionDepositorBE;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(SubscriptionDepositorBlockEntity contentHolder) {}

    @Override
    protected void addSlots() {
        int x = 11+26;
        int y = 122;

        addSlot(new CardSlot.BoundCardSlot(contentHolder.cardContainer, 0, 11+26, y)); // make here to preserve slot order

        addPlayerSlots(31+13, 165);
    }

    @Override
    protected void saveData(SubscriptionDepositorBlockEntity contentHolder) {}

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        //if (clickType == ClickType.THROW)
        //    return;
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;
        ItemStack stack = clickedSlot.getItem();

        if (index == CARD_SLOT_INDEX) { // removing card
            if (!moveItemStackTo(stack, PLAYER_INV_START_INDEX, PLAYER_INV_END_INDEX, false))
                return ItemStack.EMPTY;
        } else { // player inventory
            if (stack.getItem() instanceof CoinItem && !moveItemStackTo(stack, 0, COIN_SLOTS, false)) {
                return ItemStack.EMPTY;
            } else if (NumismaticsTags.AllItemTags.CARDS.matches(stack) && !moveItemStackTo(stack, CARD_SLOT_INDEX, CARD_SLOT_INDEX+1, false)) {
                return ItemStack.EMPTY;
            } else if (index >= PLAYER_INV_START_INDEX && index < PLAYER_HOTBAR_END_INDEX && !moveItemStackTo(stack, PLAYER_HOTBAR_END_INDEX, PLAYER_INV_END_INDEX, false)) {
                return ItemStack.EMPTY;
            } else if (index >= PLAYER_HOTBAR_END_INDEX && index < PLAYER_INV_END_INDEX && !moveItemStackTo(stack, PLAYER_INV_START_INDEX, PLAYER_HOTBAR_END_INDEX, false)) {
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }
}