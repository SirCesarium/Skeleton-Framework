package io.github.sircesarium.skeletonframework.core.visitor.item;

import io.github.sircesarium.skeletonframework.core.annotation.item.WithItemProps;
import io.github.sircesarium.skeletonframework.core.error.SkeletonReflectionException;
import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.skeletonframework.core.registry.PropertyRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Constructor;
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
    @SuppressWarnings("unchecked")
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        String itemName = (String) data.annotationData().get("value");
        Object typeRaw = data.annotationData().get("type");

        Class<? extends Item> detectedType = Item.class;

        if (typeRaw instanceof org.objectweb.asm.Type asmType) {
            try {
                detectedType = (Class<? extends Item>) Class.forName(
                        asmType.getClassName(),
                        false,
                        Thread.currentThread().getContextClassLoader()
                );
            } catch (ClassNotFoundException e) {
                throw new SkeletonReflectionException(
                        "The custom Item class defined in @SkeletonItem was not found: " + asmType.getClassName() +
                                " at field: " + field.getDeclaringClass().getName() + "#" + field.getName(), e
                );
            }
        }

        final Class<? extends Item> itemTypeForLambda = detectedType;

        validateField(field);

        try {
            Supplier<Item> supplier = getItemSupplier(field, itemTypeForLambda);

            register(itemName, supplier);

        } catch (IllegalAccessException e) {
            throw new SkeletonReflectionException("Cannot access @SkeletonItem field: " + field.getName(), e);
        }
    }

    private @NotNull Supplier<Item> getItemSupplier(Field field, Class<? extends Item> itemTypeForLambda) throws IllegalAccessException {
        Supplier<Item> supplier;

        if (Item.class.isAssignableFrom(field.getType())) {
            supplier = () -> {
                try {
                    Object v = field.get(null);
                    if (v == null) {
                        Item instance = createInstance(field, itemTypeForLambda);
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
                    "@SkeletonItem field must be Item: "
                            + field.getDeclaringClass().getName() + "#" + field.getName()
            );
        }
        return supplier;
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

    private void register(@UnknownNullability String annotation, Supplier<Item> supplier) {
        itemRegister.register(annotation, supplier);
    }

    private Item createInstance(Field field, Class<? extends Item> itemType) {
        try {
            Item.Properties props;

            if (field.isAnnotationPresent(WithItemProps.class)) {
                String propsId = field.getAnnotation(WithItemProps.class).value();

                if (propsId.isEmpty()) {
                    propsId = field.getName() + "_PROPS";
                }

                props = PropertyRegistry.getItemProps(propsId);
            } else {
                props = new Item.Properties();
            }

            Constructor<? extends Item> constructor = itemType.getConstructor(Item.Properties.class);
            return constructor.newInstance(props);

        } catch (NoSuchMethodException e) {
            throw new SkeletonReflectionException(
                    "Item class " + itemType.getName() +
                            " must have a constructor that accepts Item.Properties: " +
                            field.getDeclaringClass().getName() + "#" + field.getName(), e);
        } catch (Exception e) {
            throw e instanceof RuntimeException re ? re : new SkeletonReflectionException("Failed to create instance", e);
        }
    }
}
