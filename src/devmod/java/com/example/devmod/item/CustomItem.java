package com.example.devmod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CustomItem extends Item {
    public CustomItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        if (!context.getLevel().isClientSide) {
            player.sendSystemMessage(Component.literal("Working!"));
        }

        return InteractionResult.SUCCESS;
    }

}
