package com.example.devmod;

import com.example.devmod.item.CustomItem;
import io.github.sircesarium.beaconcore.core.annotation.generic.BeaconMod;
import io.github.sircesarium.beaconcore.core.annotation.block.RegisterBlock;
import io.github.sircesarium.beaconcore.core.annotation.item.RegisterItem;
import io.github.sircesarium.beaconcore.core.annotation.item.RegisterItemProps;
import io.github.sircesarium.beaconcore.core.annotation.item.WithItemProps;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@BeaconMod
@Mod(DevMod.MODID)
@EventBusSubscriber(Dist.DEDICATED_SERVER)
public class DevMod {

    public static final String MODID = "devmod";

    @RegisterItemProps
    public static Item.Properties MY_PROPERTIES = new Item.Properties().rarity(Rarity.EPIC);

    @RegisterItemProps()
    public static Item.Properties TEST_PROPS = new Item.Properties().fireResistant();

    @RegisterBlock("test_block")
    public static Block TEST_BLOCK;

    @RegisterItem("test_item")
    public static Item TEST_ITEM;

    @RegisterBlock(value = "test_block_without_item", withItem = false)
    public static Block TEST_BLOCK_WITHOUT_ITEM;

    @RegisterItem(value = "my_custom_item", type = CustomItem.class)
    public static Item MY_CUSTOM_ITEM;

    @RegisterItem("test")
    @WithItemProps
    public static Item TEST;

    @RegisterItem("item_with_props")
    @WithItemProps(value = "another_mod:WITH_NON_EXISTING_PROPS", fallback = "MY_PROPERTIES")
    public static Item ITEM_WITH_PROPS;

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("My Tab"))
            .icon(() -> TEST.getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(TEST.getDefaultInstance());
                output.accept(ITEM_WITH_PROPS.getDefaultInstance());
            }).build());

    public DevMod(IEventBus ev) {
        CREATIVE_MODE_TABS.register(ev);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        String haltOnStart = System.getenv("HALT_ON_START");

        if ("true".equalsIgnoreCase(haltOnStart)) {
            event.getServer().halt(false);
        }
    }
}
