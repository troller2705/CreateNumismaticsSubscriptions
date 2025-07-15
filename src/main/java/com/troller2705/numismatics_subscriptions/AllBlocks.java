package com.troller2705.numismatics_subscriptions;

import com.simibubi.create.AllTags;
import com.troller2705.numismatics_subscriptions.content.subscription_depositor.SubscriptionDepositorBlock;
import com.troller2705.numismatics_subscriptions.content.subscription_manager.SubscriptionManagerBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

@SuppressWarnings({"deprecation", "removal"})
public class AllBlocks {

    public static final BlockEntry<SubscriptionDepositorBlock> SUBSCRIPTION_DEPOSITOR = NumismaticsSubscriptions.REGISTRATE.block("subscription_depositor", SubscriptionDepositorBlock::new)
            .lang("Subscription Depositor")
//            .initialProperties(SharedProperties::wooden)
            .properties(p -> p
                    .mapColor(MapColor.PODZOL)
                    .sound(SoundType.WOOD)
                    .strength(1.4F, 3600000.0F)
                    .isRedstoneConductor((state, getter, pos) -> false)
            )
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.RELOCATION_NOT_SUPPORTED.tag)
            .simpleItem()
            .register();

    public static final BlockEntry<SubscriptionManagerBlock> SUBSCRIPTION_MANAGER = NumismaticsSubscriptions.REGISTRATE.block("subscription_manager", SubscriptionManagerBlock::new)
            .lang("Subscription Manager")
//            .initialProperties(SharedProperties::wooden)
            .properties(p -> p
                    .mapColor(MapColor.PODZOL)
                    .sound(SoundType.WOOD)
                    .strength(1.4F, 3600000.0F)
                    .isRedstoneConductor((state, getter, pos) -> false)
            )
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.RELOCATION_NOT_SUPPORTED.tag)
            .addLayer(() -> RenderType::cutoutMipped)
            .simpleItem()
            .register();


    public static void initialize(){

    }
}
