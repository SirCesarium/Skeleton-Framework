package io.github.sircesarium.skeletonframework.core;

import io.github.sircesarium.skeletonframework.SkeletonFramework;
import io.github.sircesarium.skeletonframework.core.annotation.block.SkeletonBlock;
import io.github.sircesarium.skeletonframework.core.annotation.generic.SkeletonBootstrap;
import io.github.sircesarium.skeletonframework.core.annotation.item.SkeletonItem;
import io.github.sircesarium.skeletonframework.core.reflection.AnnotationScanHelper;
import io.github.sircesarium.skeletonframework.core.reflection.ReflectionProcessor;
import io.github.sircesarium.skeletonframework.core.reflection.resolver.FieldResolver;
import io.github.sircesarium.skeletonframework.core.visitor.SkeletonBlockVisitor;
import io.github.sircesarium.skeletonframework.core.visitor.SkeletonItemVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

public class SkeletonMod {
    public static void bootstrap(IEventBus eventBus, ModContainer container) {
        AnnotationScanHelper scanner = new AnnotationScanHelper(container);

        boolean shouldSkip = scanner.find(SkeletonBootstrap.class, ElementType.TYPE).findAny().isEmpty();

        if (shouldSkip) return;

        FieldResolver fieldResolver = new FieldResolver();

        ReflectionProcessor<Field> processor = new ReflectionProcessor<>(fieldResolver);

        processor.process(
                scanner.find(SkeletonItem.class, ElementType.FIELD),
                new SkeletonItemVisitor(eventBus, container)
        );

        processor.process(
                scanner.find(SkeletonBlock.class, ElementType.FIELD),
                new SkeletonBlockVisitor(eventBus, container)
        );
    }

    public static void bootstrapAll(IEventBus eventBus) {
        for (ModContainer container : ModList.get().getSortedMods()) {
            try {
                bootstrap(eventBus, container);
            } catch (Exception e) {
                SkeletonFramework.LOGGER.error(
                        "SkeletonFramework failed processing mod '{}'",
                        container.getModId(),
                        e
                );

                throw e;
            }
        }
    }
}
