package com.example.devmod;

import io.github.sircesarium.skeletonframework.core.annotation.generic.SkeletonBootstrap;
import io.github.sircesarium.skeletonframework.core.annotation.block.SkeletonBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;

@SkeletonBootstrap
@Mod("devmod")
public class DevMod {
    public DevMod() {}
    @SkeletonBlock("test_block")
    public static Block TEST_BLOCK;
}
