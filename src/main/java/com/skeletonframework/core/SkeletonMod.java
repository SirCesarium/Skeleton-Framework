package com.skeletonframework.core;

import com.skeletonframework.SkeletonFramework;
import com.skeletonframework.core.annotation.item.SkeletonItem;
import com.skeletonframework.core.reflection.AnnotationScanHelper;
import com.skeletonframework.core.reflection.ReflectionProcessor;
import com.skeletonframework.core.reflection.resolver.FieldResolver;
import com.skeletonframework.core.visitor.SkeletonItemVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

public class SkeletonMod {
    public static void bootstrap(IEventBus eventBus, ModContainer container) {
        AnnotationScanHelper scanner = new AnnotationScanHelper(container);

        FieldResolver fieldResolver = new FieldResolver();

        ReflectionProcessor<Field> processor = new ReflectionProcessor<>(fieldResolver);

        processor.process(
                scanner.find(SkeletonItem.class, ElementType.FIELD),
                new SkeletonItemVisitor(eventBus, container)
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
