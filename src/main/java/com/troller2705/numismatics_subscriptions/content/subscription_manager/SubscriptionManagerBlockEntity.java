package com.troller2705.numismatics_subscriptions.content.subscription_manager;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.troller2705.numismatics_subscriptions.AllBlocks;
import com.troller2705.numismatics_subscriptions.AllItems;
import com.troller2705.numismatics_subscriptions.AllMenuTypes;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list.OpenSubsListPacket;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedAccountData;
import com.troller2705.numismatics_subscriptions.content.backend.ExtendedBankAccountBehaviour;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list.SubsListHolder;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.subs_list.SubsListMenu;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.content.backend.Trusted;
import dev.ithundxr.createnumismatics.content.backend.trust_list.TrustListContainer;
import dev.ithundxr.createnumismatics.content.backend.trust_list.TrustListHolder;
import dev.ithundxr.createnumismatics.content.backend.trust_list.TrustListMenu;
import dev.ithundxr.createnumismatics.registry.NumismaticsBlocks;
import dev.ithundxr.createnumismatics.registry.NumismaticsItems;
import dev.ithundxr.createnumismatics.registry.packets.OpenTrustListPacket;
import dev.ithundxr.createnumismatics.util.Utils;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriptionManagerBlockEntity extends SmartBlockEntity implements Trusted, TrustListHolder, SubsListHolder, MenuProvider {

    protected LerpedFloat headAnimation;
    protected LerpedFloat headAngle;

    // only available on client
    private int clientsideBalance = 0;

    // only available on server
    private int serversideBalance = 0;

    protected ExtendedBankAccountBehaviour bankAccountBehaviour;

    @Nullable
    protected UUID owner;

    public String getLabel() {
        if (label != null && label.isEmpty())
            label = null;
        return label;
    }

    public void setLabel(String label) {
        if (label.isEmpty())
            label = null;
        if (level != null && !level.isClientSide) {
            getAccount().setLabel(label);
        }
        this.label = label;
        notifyUpdate();
    }

    @Nullable
    protected String label;

    protected final List<UUID> trustList = new ArrayList<>();

    public final TrustListContainer trustListContainer = new TrustListContainer(trustList, this::onTrustListChanged);

    private boolean delayedDataSync = false;

    public SubscriptionManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        headAnimation = LerpedFloat.linear();
        headAngle = LerpedFloat.angular();
    }

    // copied from Create's Blaze Burner
    @OnlyIn(Dist.CLIENT)
    private void tickAnimation() {
        boolean active = Minecraft.getInstance().screen instanceof SubscriptionManagerScreen;

        {
            float target = 0;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.isInvisible()) {
                double x;
                double z;
                if (isVirtual()) {
                    x = -4;
                    z = -10;
                } else {
                    x = player.getX();
                    z = player.getZ();
                }
                double dx = x - (getBlockPos().getX() + 0.5);
                double dz = z - (getBlockPos().getZ() + 0.5);
                target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
            }
            target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
            headAngle.chase(target, .25f, LerpedFloat.Chaser.exp(5));
            headAngle.tickChaser();
        }

        headAnimation.chase(active ? 1 : 0, .25f, LerpedFloat.Chaser.exp(.25f));
        headAnimation.tickChaser();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        bankAccountBehaviour = new ExtendedBankAccountBehaviour(this);
        behaviours.add(bankAccountBehaviour);
    }

    private void onTrustListChanged() {
        if (level == null || level.isClientSide)
            return;
        BankAccount account = getAccount();
        account.updateTrustList((accountTrustList) -> {
            accountTrustList.clear();
            accountTrustList.add(owner);
            accountTrustList.addAll(trustList);
        });
        notifyUpdate();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null || level.isClientSide)
            return;
        if (delayedDataSync) {
            delayedDataSync = false;
            sendData();
        }

        var _extAcc = getExtendedAccount();

        if (owner != null && !getAccount().isAuthorized(owner)) {
            onTrustListChanged();
        }

        if (serversideBalance != getAccount().getBalance()) {
            serversideBalance = getAccount().getBalance();
            sendData();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && level.isClientSide) {
            tickAnimation();
            if (!isVirtual())
                spawnParticles(BlazeBurnerBlock.HeatLevel.KINDLED, 1);
            return;
        }
    }

    // copied from Create's Blaze Burner
    protected void spawnParticles(BlazeBurnerBlock.HeatLevel heatLevel, double burstMult) {
        if (level == null)
            return;
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE)
            return;

        RandomSource r = level.getRandom();

        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
                .multiply(1, 0, 1));

        if (r.nextInt(4) != 0)
            return;

        boolean empty = level.getBlockState(worldPosition.above())
                .getCollisionShape(level, worldPosition.above())
                .isEmpty();

        if (empty || r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);

        double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, .25f, 1)
                        .normalize()
                        .scale((empty ? .25f : .5) + r.nextDouble() * .125f))
                .add(0, .5, 0);

        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        } else if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            level.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        }
        return;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (owner != null)
            tag.putUUID("Owner", owner);

        if (!trustListContainer.isEmpty()) {
            tag.put("TrustListInv", trustListContainer.save(new CompoundTag(), registries));
        }
        if (getLabel() != null)
            tag.putString("Label", getLabel());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;

        trustListContainer.clearContent();
        trustList.clear();
        if (tag.contains("TrustListInv", Tag.TAG_COMPOUND)) {
            trustListContainer.load(tag.getCompound("TrustListInv"), registries);
        }

        setLabel(tag.getString("Label"));
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

    public BankAccount getAccount() {
        if (this.isRemoved()) {
            Numismatics.LOGGER.error("Tried to get account from removed banker!");
            return null;
        }
        if (bankAccountBehaviour == null) {
            return null;
        }
        return bankAccountBehaviour.getAccount();
    }

    public boolean hasAccount() {
        if (this.isRemoved()) {
            Numismatics.LOGGER.error("Tried to check account from removed banker!");
            return false;
        }
        if (bankAccountBehaviour == null) {
            return false;
        }
        return bankAccountBehaviour.hasAccount();
    }

    @Override
    public boolean isTrustedInternal(Player player) {
        if (Utils.isDevEnv()) { // easier to test this way in dev
            return player.getItemBySlot(EquipmentSlot.FEET).is(Items.GOLDEN_BOOTS);
        } else {
            return owner == null || owner.equals(player.getUUID()) || trustList.contains(player.getUUID());
        }
    }

    @Override
    public ImmutableList<UUID> getTrustList() {
        return ImmutableList.copyOf(trustList);
    }

    @Override
    public Container getTrustListBackingContainer() {
        return trustListContainer;
    }

    void notifyDelayedDataSync() {
        delayedDataSync = true;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.numismatics_subscriptions.subscription_manager");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new SubscriptionManagerMenu(AllMenuTypes.SUBSCRIPTION_MANAGER.get(), i, inventory, this);
    }

    @NotNull
    public String getLabelNonNull() {
        return getLabel() == null ? "Subscription Manager" : getLabel();
    }

    public UUID getAccountId() {
        if (bankAccountBehaviour == null) {
            return null;
        }
        return bankAccountBehaviour.getAccountUUID();
    }

    public int getClientsideBalance() {
        return clientsideBalance;
    }

    public void openMenu(ServerPlayer player) {
        if (!isTrusted(player)) {
            return;
        }
        if (level == null || level.isClientSide)
            return;
        Utils.openScreen(player, this, this::sendToMenu);
    }

    public int getTotalPrice() {
        return bankAccountBehaviour.getTotalPrice();
    }

    public int getPrice(Coin coin) {
        return bankAccountBehaviour.getPrice(coin);
    }

    public Integer[] getPrices(){
        return bankAccountBehaviour.getPrices();
    }

    public void setPrice(Coin coin, int price) {

        if (level != null && !level.isClientSide) {
            getExtendedAccount().getCoinPrice().setPrice(coin, price);
        }

        bankAccountBehaviour.setPrice(coin, price);

        notifyUpdate();
    }

    public int getInterval(){ return bankAccountBehaviour.getInterval(); }

    public void setInterval(int interval) {

        if (level != null && !level.isClientSide) {
            getExtendedAccount().setInterval(interval);
        }
        bankAccountBehaviour.setInterval(interval);

        notifyUpdate();
    }

    public String getUnit(){ return bankAccountBehaviour.getUnit(); }

    public void setUnit(String unit) {

        if (level != null && !level.isClientSide) {
            getExtendedAccount().setUnit(unit);
        }
        bankAccountBehaviour.setUnit(unit);

        notifyUpdate();
    }

    public String getAllowedAccountType(){ return bankAccountBehaviour.getAllowedAccountType(); }

    public void setAllowedAccountType(String allowedAccountType) {

        if (level != null && !level.isClientSide) {
            getExtendedAccount().setAllowedAccountType(allowedAccountType);
        }
        bankAccountBehaviour.setAllowedAccountType(allowedAccountType);

        notifyUpdate();
    }

    public void openTrustList()
    {
        if (level == null || !level.isClientSide)
            return;
        CatnipServices.NETWORK.sendToServer(new OpenTrustListPacket<>(getBlockPos()));
    }

    public void openSubsList()
    {
        if (level == null || !level.isClientSide)
            return;
        CatnipServices.NETWORK.sendToServer(new OpenSubsListPacket<>(getBlockPos()));
    }

    public void openTrustListMenu(ServerPlayer player) {
        TrustListMenu.openMenu(this, player, NumismaticsItems.ID_CARDS.get(DyeColor.RED).asStack());
    }

    public void openSubsListMenu(ServerPlayer player) {
        SubsListMenu.openMenu(this, player, AllItems.SUBSCRIPTION_GUIDE.asStack());
    }
}
