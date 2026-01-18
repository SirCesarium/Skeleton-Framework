package io.github.sircesarium.skeletonframework.core.annotation.generic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.github.sircesarium.skeletonframework.core.annotation.item.SkeletonItem;
import io.github.sircesarium.skeletonframework.core.annotation.block.SkeletonBlock;

/**
 * Marks a mod entrypoint class to be processed by SkeletonFramework.
 * Only classes annotated with {@link SkeletonBootstrap} will have their
 * {@link SkeletonItem}, {@link SkeletonBlock}, etc. automatically registered.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SkeletonBootstrap {
}
