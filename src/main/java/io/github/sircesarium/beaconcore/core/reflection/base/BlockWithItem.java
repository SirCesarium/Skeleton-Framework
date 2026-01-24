package io.github.sircesarium.beaconcore.core.reflection.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record BlockWithItem(
        Block block,
        Item item
) {
}
