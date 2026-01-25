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

        validateField(field);

        Class<? extends Item> itemType = (Class<? extends Item>) field.getType();

        try {
            Supplier<Item> supplier = getItemSupplier(field, itemType);
            register(itemName, supplier);
        } catch (IllegalAccessException e) {
            throw new BeaconReflectionException("Cannot access @RegisterItem field: " + field.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private @NotNull Supplier<Item> getItemSupplier(Field field, Class<?> fieldType) throws IllegalAccessException {
        return () -> {
            try {
                Object v = field.get(null);

                if (v instanceof Supplier<?> supplier) {
                    Object suppliedValue = supplier.get();
                    if (suppliedValue instanceof Item item) {
                        return item;
                    }
                    throw new BeaconReflectionException("Supplier in field '" + field.getName() + "' did not return an Item");
                }

                if (v instanceof Item item) {
                    return item;
                }

                if (v == null) {
                    if (Modifier.isFinal(field.getModifiers())) {
                        throw new BeaconReflectionException(
                                "Field '" + field.getName() + "' is final and null. " +
                                        "To use manual instantiation, use BeaconHolder<Item>. " +
                                        "To use auto-injection, remove 'final'."
                        );
                    }

                    Item instance = createInstance(field, (Class<? extends Item>) fieldType);
                    field.set(null, instance);
                    return instance;
                }

                throw new BeaconReflectionException("Unsupported field type for @RegisterItem: " + field.getName());

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        };
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
