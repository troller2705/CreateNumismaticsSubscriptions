package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.bank.CardItem;
import dev.ithundxr.createnumismatics.content.bank.CardSlot;
import dev.ithundxr.createnumismatics.content.bank.blaze_banker.BlazeBankerMenu;
import dev.ithundxr.createnumismatics.content.coins.CoinDisplaySlot;
import dev.ithundxr.createnumismatics.content.coins.CoinItem;
import dev.ithundxr.createnumismatics.registry.NumismaticsTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class SubscriptionManagerMenu extends MenuBase<SubscriptionManagerBlockEntity> {
    public static final int COIN_SLOTS = Coin.values().length;
    public static final int CARD_SLOT_INDEX = COIN_SLOTS;
    public static final int PLAYER_INV_START_INDEX = CARD_SLOT_INDEX + 1;
    public static final int PLAYER_HOTBAR_END_INDEX = PLAYER_INV_START_INDEX + 9;
    public static final int PLAYER_INV_END_INDEX = PLAYER_INV_START_INDEX + 36;

    private CardWritingContainer cardWritingContainer;

    public SubscriptionManagerMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public SubscriptionManagerMenu(MenuType<?> type, int id, Inventory inv, SubscriptionManagerBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    @Override
    protected SubscriptionManagerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof SubscriptionManagerBlockEntity subscriptionDepositorBE) {
            subscriptionDepositorBE.readClient(Objects.requireNonNull(extraData.readNbt()), extraData.registryAccess());
            return subscriptionDepositorBE;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(SubscriptionManagerBlockEntity contentHolder) {
    }

    @Override
    protected void addSlots() {
        if (cardWritingContainer == null)
            cardWritingContainer = new SubscriptionManagerMenu.CardWritingContainer(this::slotsChanged, contentHolder.getAccountId());
        int x = 39+13;
        int y = 143;

        addSlot(new CardSlot.UnboundCardSlot(cardWritingContainer, 0, x, y)); // make here to preserve slot order

        addPlayerSlots(31+13, 186);

        // label coins

        int labelX1 = 12+13;
        int labelX2 = labelX1 + 86;
        int labelY = 46;
        int labelYIncrement = 22;

        for (int i = 0; i < 6; i++) {
            Coin coin = Coin.values()[i];
            int slotX = i < 3 ? labelX1 : labelX2;
            int slotY = labelY + ((i%3) * labelYIncrement);

            addSlot(new CoinDisplaySlot(coin, slotX, slotY));
        }
    }

    @Override
    protected void saveData(SubscriptionManagerBlockEntity contentHolder) {}

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

    private static class CardWritingContainer implements Container
    {
        private final Consumer<CardWritingContainer> slotsChangedCallback;
        private final UUID uuid;

        @NotNull
        protected final List<ItemStack> stacks = new ArrayList<>();

        public CardWritingContainer(Consumer<CardWritingContainer> slotsChangedCallback, UUID uuid) {
            this.slotsChangedCallback = slotsChangedCallback;
            this.uuid = uuid;
            stacks.add(ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        protected ItemStack getStack() {
            return stacks.get(0);
        }

        @Override
        public boolean isEmpty() {
            return getStack().isEmpty();
        }

        @Override
        public @NotNull ItemStack getItem(int slot) {
            return getStack();
        }

        @Override
        public @NotNull ItemStack removeItem(int slot, int amount) {
            ItemStack stack = ContainerHelper.removeItem(this.stacks, 0, amount);
            if (!stack.isEmpty()) {
                this.slotsChangedCallback.accept(this);
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(this.stacks, 0);
        }

        @Override
        public void setItem(int slot, @NotNull ItemStack stack) {
            this.stacks.set(0, stack);
            if (!CardItem.isBound(stack) && NumismaticsTags.AllItemTags.CARDS.matches(stack))
                CardItem.set(stack, uuid);
            this.slotsChangedCallback.accept(this);
        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(@NotNull Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            this.stacks.set(0, ItemStack.EMPTY);
        }
    }
}