package io.github.sircesarium.beaconcore.core.visitor.item;

import io.github.sircesarium.beaconcore.BeaconCore;
import io.github.sircesarium.beaconcore.core.annotation.item.WithItemProps;
import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.beaconcore.core.registry.PropertyRegistry;
import io.github.sircesarium.beaconcore.core.registry.RegistryManager;
import net.minecraft.core.registries.Registries;
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

public final class ItemRegistryVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister<Item> itemRegister;
    private final String modNamespace;

    public ItemRegistryVisitor(IEventBus eventBus, ModContainer container) {
        this.itemRegister = RegistryManager.getOrCreate(eventBus, container.getModId(), Registries.ITEM);
        this.modNamespace = container.getModId();
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
                throw new BeaconReflectionException(
                        "The custom Item class defined in @RegisterItem was not found: " + asmType.getClassName() +
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
            throw new BeaconReflectionException("Cannot access @RegisterItem field: " + field.getName(), e);
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
            throw new BeaconReflectionException(
                    "@RegisterItem field must be Item: "
                            + field.getDeclaringClass().getName() + "#" + field.getName()
            );
        }
        return supplier;
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new BeaconReflectionException(
                    "@RegisterItem field must be static: "
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
            Item.Properties props = null;
            WithItemProps annotation = field.getAnnotation(WithItemProps.class);

            if (annotation != null) {
                String propsId = annotation.value().isEmpty() ? field.getName() + "_PROPS" : annotation.value();
                String fallbackId = annotation.fallback();

                String fullPropsId = normalizeId(propsId, modNamespace);
                boolean isValueExternal = propsId.contains(":");

                if (!fallbackId.isEmpty()) {
                    String fullFallbackId = normalizeId(fallbackId, modNamespace);
                    boolean isFallbackExternal = fallbackId.contains(":");

                    if (!PropertyRegistry.existsInItemProps(fullFallbackId)) {
                        if (!isFallbackExternal) {
                            throw new BeaconReflectionException(
                                    "Local fallback property '" + fullFallbackId + "' not found for field: " + field.getName());
                        } else {
                            BeaconCore.LOGGER.warn(
                                    "External fallback property '{}' not found for field: {}", fullFallbackId, field.getName());
                        }
                    }
                }

                if (PropertyRegistry.existsInItemProps(fullPropsId)) {
                    props = PropertyRegistry.getItemProps(fullPropsId);
                } else {
                    if (!isValueExternal) {
                        throw new BeaconReflectionException(
                                "Local property '" + fullPropsId + "' not found for field: " + field.getName());
                    } else {
                        BeaconCore.LOGGER.warn(
                                "External property '{}' not found for field: {}. Attempting fallback...", fullPropsId, field.getName());
                    }

                    if (!fallbackId.isEmpty()) {
                        String fullFallbackId = normalizeId(fallbackId, modNamespace);
                        if (PropertyRegistry.existsInItemProps(fullFallbackId)) {
                            props = PropertyRegistry.getItemProps(fullFallbackId);
                        }
                    }
                }

                if (props == null) {
                    props = PropertyRegistry.getItemProps(fullPropsId);
                }

            } else {
                props = new Item.Properties();
            }

            Constructor<? extends Item> constructor = itemType.getConstructor(Item.Properties.class);
            return constructor.newInstance(props);

        } catch (Exception e) {
            throw e instanceof RuntimeException re ? re : new BeaconReflectionException("Failed to create instance for " + field.getName(), e);
        }
    }

    private static String normalizeId(String id, String defaultNamespace) {
        if (id == null || id.isEmpty()) return "";
        return id.contains(":") ? id : defaultNamespace + ":" + id;
    }
}
