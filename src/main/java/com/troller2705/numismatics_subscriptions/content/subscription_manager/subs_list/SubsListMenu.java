package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.troller2705.numismatics_subscriptions.AllMenuTypes;
import com.troller2705.numismatics_subscriptions.content.backend.SubscriptionsBankData;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerBlockEntity;
import dev.ithundxr.createnumismatics.content.backend.Trusted;
import dev.ithundxr.createnumismatics.util.Utils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SubsListMenu extends MenuBase<SubsListHolder>
{
    ItemStack renderedItem;

    public SubsListMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData)
    {
        super(type, id, inv, extraData);
        if (inv.player.level() instanceof ServerLevel serverLevel) {
            var data = SubscriptionsBankData.get(serverLevel).getAll();
            List<Pair<String, Boolean>> list = new ArrayList<>();

            for (Map.Entry<UUID, Boolean> entry : data.entrySet()) {
                GameProfile profile = serverLevel.getServer().getProfileCache()
                        .get(entry.getKey())
                        .orElse(null);

                String name = profile != null ? profile.getName() : entry.getKey().toString();
                list.add(Pair.of(name, entry.getValue()));
            }
        }
    }

    protected SubsListMenu(MenuType<?> type, int id, Inventory inv, SubsListHolder contentHolder, ItemStack renderedItem) {
        super(type, id, inv, contentHolder);
        this.renderedItem = renderedItem;
        if (inv.player.level() instanceof ServerLevel serverLevel) {
            var data = SubscriptionsBankData.get(serverLevel).getAll();
            List<Pair<String, Boolean>> list = new ArrayList<>();

            for (Map.Entry<UUID, Boolean> entry : data.entrySet()) {
                GameProfile profile = Objects.requireNonNull(serverLevel.getServer().getProfileCache())
                        .get(entry.getKey())
                        .orElse(null);

                String name = profile != null ? profile.getName() : entry.getKey().toString();
                list.add(Pair.of(name, entry.getValue()));
            }
        }
    }

    @Override
    protected SubsListHolder createOnClient(RegistryFriendlyByteBuf extraData) {return null;}

    @Override
    protected void initAndReadInventory(SubsListHolder contentHolder)
    {

    }

    @Override
    protected void addSlots() {}

    @Override
    protected void saveData(SubsListHolder contentHolder)
    {

    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {return null;}

    public static MenuProvider provider(SubsListHolder contentHolder, ItemStack renderedItem) {
        return new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable("gui.numismatics.trust_list");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new SubsListMenu(AllMenuTypes.SUBS_LIST.get(), i, inventory, contentHolder, renderedItem);
            }
        };
    }

    public static <BE extends SmartBlockEntity & MenuProvider & Trusted & SubsListHolder> void openMenu(SubscriptionManagerBlockEntity be, ServerPlayer player, ItemStack displayStack) {
        if (be.isTrusted(player)) {
            Utils.openScreen(player,
                    SubsListMenu.provider(be, displayStack),
                    (buf) -> {
                        ItemStack.STREAM_CODEC.encode(buf, displayStack);
                        be.sendToMenu(buf);
                    });
        }
    }
}
