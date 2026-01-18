package io.github.sircesarium.skeletonframework;

import io.github.sircesarium.skeletonframework.core.SkeletonMod;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(SkeletonFramework.MOD_ID)
public class SkeletonFramework {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "skeletonframework";

    public SkeletonFramework(IEventBus eventBus) {
        SkeletonMod.bootstrapAll(eventBus);
    }
}
