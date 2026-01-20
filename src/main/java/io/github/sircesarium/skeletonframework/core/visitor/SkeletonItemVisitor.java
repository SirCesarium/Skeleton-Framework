package io.github.sircesarium.skeletonframework.core.visitor;

import io.github.sircesarium.skeletonframework.core.annotation.item.SkeletonItem;
import io.github.sircesarium.skeletonframework.core.error.SkeletonReflectionException;
import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectionVisitor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class SkeletonItemVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister.Items itemRegister;

    public SkeletonItemVisitor(IEventBus eventBus, ModContainer container) {
        this.itemRegister = DeferredRegister.createItems(container.getModId());
        this.itemRegister.register(eventBus);
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        SkeletonItem annotation = field.getAnnotation(SkeletonItem.class);
        validateField(field);

        try {
            Object value = field.get(null);

            Supplier<Item> supplier;

            if (value instanceof Supplier<?> sup) {
                supplier = () -> (Item) sup.get();
            } else if (Item.class.isAssignableFrom(field.getType())) {supplier = () -> {
                try {
                    Object v = field.get(null);
                    if (v == null) {
                        Item instance = createInstance(field);
                        field.set(null, instance);
                        return instance;
                    }
                    return (Item) v;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to auto-instantiate field: " + field.getName(), e);
                }
            };
            } else {
                throw new SkeletonReflectionException(
                        "@SkeletonItem field must be Item or Supplier<Item>: "
                                + field.getDeclaringClass().getName()
                                + "#" + field.getName()
                );
            }

            register(annotation, supplier, annotation.type());

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

    private void register(SkeletonItem annotation, Supplier<Item> supplier, Class<? extends Item> itemClass) {
        itemRegister.register(annotation.value(), supplier);
    }

    private Item createInstance(Field field) {
        // TODO: Check if annotated with @WithItemProps
        // if (field.isAnnotationPresent(WithItemProps.class)) { ... }

        return new Item(new Item.Properties());
    }
}
