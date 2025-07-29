package com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.troller2705.numismatics_subscriptions.AllMenuTypes;
import com.troller2705.numismatics_subscriptions.content.backend.SubscriptionStatus;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerBlockEntity;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.Trusted;
import dev.ithundxr.createnumismatics.util.UsernameUtils;
import dev.ithundxr.createnumismatics.util.Utils;
import net.createmod.catnip.platform.CatnipServices;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SubsListMenu extends MenuBase<SubsListHolder>
{
    ItemStack renderedItem;

    public SubsListMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData)
    {
        super(type, id, inv, extraData);

    }

    public SubsListMenu(MenuType<?> type, int id, Inventory inv, SubsListHolder contentHolder, ItemStack renderedItem) {
        super(type, id, inv, contentHolder);
        this.renderedItem = renderedItem;
        if (inv.player.level() instanceof ServerLevel serverLevel) {
            var data = ((SubscriptionManagerBlockEntity) contentHolder).getExtendedAccount().getAll();
            List<SubscriptionStatus> subscribers = new ArrayList<>();

            //TODO: Dev only
            data.put(inv.player.getUUID(), true);
            data.put(UUID.fromString("0c069de8-e1ce-46be-b67a-c2f06dfb40fc"), false);
            data.put(UUID.fromString("7ab87465-0ab9-4b72-9412-a54690d3291b"), false);
            data.put(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"), false);
            data.put(UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"), false);
            data.put(UUID.fromString("ea4c7b79-1eea-445c-aa73-75f9bf077cb0"), false);
            data.put(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), false);
            data.put(UUID.fromString("bbb87dbe-690f-4205-bdc5-72ffb8ebc29d"), false);
            data.put(UUID.fromString("4f3a8d1e-33c1-44e7-bce8-e683027c7dac"), false);
            data.put(UUID.fromString("5f8eb73b-25be-4c5a-a50f-d27d65e30ca0"), false);
            data.put(UUID.fromString("5a1839d2-cecc-4c85-aa08-b346f9f772a1"), false);
            data.put(UUID.fromString("93b459be-ce4f-4700-b457-c1aa91b3b687"), false);
            data.put(UUID.fromString("cae9554c-31be-47e2-ba2b-4b8867adacc5"), false);

            for (Map.Entry<UUID, Boolean> entry : data.entrySet()) {
                BankAccount account = Numismatics.BANK.getAccount(entry.getKey());

                if(account != null){
                    String label = null;
                    switch (account.type){
                        case PLAYER:
                            label = UsernameUtils.INSTANCE.getName(account.id);
                            break;
                        default:
                            label = account.getLabel();
                            break;
                    }

                    subscribers.add(new SubscriptionStatus(account.id, label, entry.getValue()));
                }else{
                    subscribers.add(new SubscriptionStatus(entry.getKey(), UsernameUtils.INSTANCE.getName(entry.getKey()), entry.getValue()));
                }
            }

            if(inv.player instanceof ServerPlayer serverPlayer)
                CatnipServices.NETWORK.sendToClient(serverPlayer, new SyncSubscribersPacket(subscribers));
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
                return Component.translatable("gui.numismatics_subscriptions.subs_list");
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
