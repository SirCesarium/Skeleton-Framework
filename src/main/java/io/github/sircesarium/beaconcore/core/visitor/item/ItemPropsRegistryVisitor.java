package io.github.sircesarium.beaconcore.core.visitor.item;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.beaconcore.core.registry.PropertyRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ItemPropsRegistryVisitor implements ReflectionVisitor<Field> {

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        String propsId = (String) data.annotationData().get("value");

        if (propsId == null || propsId.isEmpty()) {
            propsId = field.getName();
        }

        validateField(field);

        try {
            Object value = field.get(null);
            if (value instanceof Item.Properties props) {
                PropertyRegistry.saveItemProps(propsId, props);
            } else {
                throw new BeaconRegistryException(
                        "Field '" + field.getName() + "' is not Item.Properties"
                );
            }
        } catch (IllegalAccessException e) {
            throw new BeaconReflectionException("Error accessing field", e);
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new BeaconRegistryException(
                    "@RegisterItemProps field must be static: "
                            + field.getDeclaringClass().getName() + "#" + field.getName()
            );
        }
    }
}