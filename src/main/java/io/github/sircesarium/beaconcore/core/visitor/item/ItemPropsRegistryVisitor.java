package io.github.sircesarium.beaconcore.core.visitor.item;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.beaconcore.core.registry.PropertyRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class ItemPropsRegistryVisitor implements ReflectionVisitor<Field> {
    private final String modNamespace;

    public ItemPropsRegistryVisitor(ModContainer container) {
        this.modNamespace = container.getModId();
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        validateField(field);

        String propsId = (String) data.annotationData().getOrDefault("value", field.getName());
        String namespace = (String) data.annotationData().getOrDefault("namespace", modNamespace);

        try {
            Object value = field.get(null);

            if (value instanceof Item.Properties props) {
                PropertyRegistry.saveItemProps(namespace + ":" + propsId, () -> props);
            } else if (value instanceof Supplier<?> supplier) {
                PropertyRegistry.saveItemProps(namespace + ":" + propsId, () -> {
                    Object supplied = supplier.get();
                    if (!(supplied instanceof Item.Properties p)) {
                        throw new BeaconRegistryException("Supplier '" + field.getName() + "' must return Item.Properties");
                    }
                    return p;
                });
            } else {
                throw new BeaconRegistryException(
                        "Field '" + field.getName() + "' in " + field.getDeclaringClass().getSimpleName() +
                                " must be Item.Properties or Supplier<Item.Properties>"
                );
            }

        } catch (IllegalAccessException e) {
            throw new BeaconReflectionException("Failed to access static field: " + field.getName(), e);
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new BeaconRegistryException(
                    "@RegisterItemProps field must be static: " +
                            field.getDeclaringClass().getName() + "#" + field.getName()
            );
        }
    }
}