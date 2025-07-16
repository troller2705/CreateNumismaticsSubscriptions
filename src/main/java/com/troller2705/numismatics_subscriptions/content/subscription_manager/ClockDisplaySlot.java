package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ClockDisplaySlot extends Slot {
    private static final Container DUMMY_INVENTORY = new SimpleContainer(1); // Must have at least 1 slot
    private final ItemStack displayStack;

    public ClockDisplaySlot(int xPosition, int yPosition) {
        super(DUMMY_INVENTORY, 0, xPosition, yPosition);
        this.displayStack = new ItemStack(Items.CLOCK);
        DUMMY_INVENTORY.setItem(0, displayStack.copy()); // Preload the dummy slot to avoid sync issues
    }

    @Override
    public @NotNull ItemStack getItem() {
        return displayStack.copy();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }
}
