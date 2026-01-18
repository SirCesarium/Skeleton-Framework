package com.skeletonframework.core.visitor;

import com.skeletonframework.core.annotation.item.SkeletonItem;
import com.skeletonframework.core.error.SkeletonReflectionException;

import com.skeletonframework.core.reflection.base.ReflectionVisitor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class SkeletonItemVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister<Item> itemRegister;

    public SkeletonItemVisitor(IEventBus eventBus, ModContainer container) {
        this.itemRegister = DeferredRegister.create(Registries.ITEM, container.getModId());
        this.itemRegister.register(eventBus);
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        SkeletonItem annotation = field.getAnnotation(SkeletonItem.class);

        validateField(field);

        try {
            Object value = field.get(null);

            if (value instanceof Supplier<?> supplier) {
                register(annotation, () -> (Item) supplier.get());
            } else if (value instanceof Item item) {
                register(annotation, () -> item);
            } else {
                throw new SkeletonReflectionException(
                        "@SkeletonItem field must be Item or Supplier<Item>: "
                                + field.getDeclaringClass().getName()
                                + "#" + field.getName()
                );
            }

        } catch (IllegalAccessException e) {
            throw new SkeletonReflectionException(
                    "Cannot access @SkeletonItem field: " + field.getName(), e
            );
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new SkeletonReflectionException(
                    "@SkeletonItem field must be static: "
                            + field.getDeclaringClass().getName()
                            + "#" + field.getName()
            );
        }
    }

    private void register(SkeletonItem annotation, Supplier<Item> supplier) {
        itemRegister.register(annotation.value(), supplier);
    }
}
