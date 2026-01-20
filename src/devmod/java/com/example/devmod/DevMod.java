package com.example.devmod;

import com.example.devmod.item.CustomItem;
import io.github.sircesarium.skeletonframework.core.annotation.generic.SkeletonBootstrap;
import io.github.sircesarium.skeletonframework.core.annotation.block.SkeletonBlock;
import io.github.sircesarium.skeletonframework.core.annotation.item.SkeletonItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.function.Supplier;

@SkeletonBootstrap
@Mod("devmod")
@EventBusSubscriber(Dist.DEDICATED_SERVER)
public class DevMod {
    public DevMod() {}

    @SkeletonBlock("test_block")
    public static Block TEST_BLOCK;

    @SkeletonItem("test_item")
    public static Item TEST_ITEM;

    @SkeletonBlock("test_block_with_supplier")
    public static Supplier<Block> TEST_BLOCK_SUPPLIER = () -> new Block(BlockBehaviour.Properties.of().jumpFactor(5.0f));

    @SkeletonItem("test_item_with_supplier")
    public static Supplier<Item> TEST_ITEM_SUPPLIER = () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().build()));

    @SkeletonBlock(value = "test_block_without_item", withItem = false)
    public static Block TEST_BLOCK_WITHOUT_ITEM;

    @SkeletonItem(value = "my_custom_item", type = CustomItem.class)
    public static Item MY_CUSTOM_ITEM;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        String haltOnStart = System.getenv("HALT_ON_START");

        if ("true".equalsIgnoreCase(haltOnStart)) {
            event.getServer().halt(false);
        }
    }
}
