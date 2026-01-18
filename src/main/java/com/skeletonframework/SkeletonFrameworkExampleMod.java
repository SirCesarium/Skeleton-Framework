package com.skeletonframework;

import com.skeletonframework.core.SkeletonMod;
import com.skeletonframework.core.annotation.item.SkeletonItem;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

import java.util.function.Supplier;

@Mod(SkeletonFrameworkExampleMod.MOD_ID)
public class SkeletonFrameworkExampleMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "skeletonframework";

    public SkeletonFrameworkExampleMod(IEventBus eventBus, ModContainer modContainer) {
        SkeletonMod.bootstrap(eventBus, modContainer);
    }

    @SkeletonItem("example_item")
    public static final Supplier<Item> EXAMPLE =
            () -> new Item(new Item.Properties());
}
