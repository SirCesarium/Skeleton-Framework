package com.skeletonframework;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(SkeletonFramework.MOD_ID)
public class SkeletonFramework {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "skeletonframework";

    public SkeletonFramework(IEventBus eventBus, ModContainer modContainer) {
    }
}
