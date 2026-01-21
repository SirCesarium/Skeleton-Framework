package io.github.sircesarium.skeletonframework.core.visitor.item;

import io.github.sircesarium.skeletonframework.core.error.SkeletonReflectionException;
import io.github.sircesarium.skeletonframework.core.error.SkeletonRegistryException;
import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.skeletonframework.core.registry.PropertyRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class SkeletonItemPropsVisitor implements ReflectionVisitor<Field> {

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
                throw new SkeletonRegistryException(
                        "Field '" + field.getName() + "' is not Item.Properties"
                );
            }
        } catch (IllegalAccessException e) {
            throw new SkeletonReflectionException("Error accessing field", e);
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new SkeletonRegistryException(
                    "@SkeletonItemProps field must be static: "
                            + field.getDeclaringClass().getName() + "#" + field.getName()
            );
        }
    }
}