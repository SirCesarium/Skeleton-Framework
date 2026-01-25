package io.github.sircesarium.beaconcore.core.reflection.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

@SuppressWarnings("unused")
public record BlockWithItem(
        DeferredHolder<Block, Block> blockHolder,
        DeferredHolder<Item, ?> itemHolder
) {
    public Block block() { return blockHolder.get(); }
    public Item item() { return itemHolder.get(); }

    public record CustomBlock<T extends Block>(
            DeferredHolder<Block, T> blockHolder,
            DeferredHolder<Item, ?> itemHolder
    ) {
        public T block() { return blockHolder.get(); }
        public Item item() { return itemHolder.get(); }
    }
}