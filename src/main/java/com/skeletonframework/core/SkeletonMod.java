package com.skeletonframework.core;

import com.skeletonframework.core.annotation.item.SkeletonItem;
import com.skeletonframework.core.reflection.AnnotationScanHelper;
import com.skeletonframework.core.reflection.ReflectionProcessor;
import com.skeletonframework.core.reflection.resolver.FieldResolver;
import com.skeletonframework.core.visitor.SkeletonItemVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

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
}
