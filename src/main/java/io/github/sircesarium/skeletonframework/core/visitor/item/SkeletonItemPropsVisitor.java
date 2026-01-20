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

        validateField(field);

        try {
            Object value = field.get(null);

            if (value instanceof Item.Properties props) {
                PropertyRegistry.saveItemProps(propsId, props);
            } else {
                throw new SkeletonRegistryException(
                        "Field '" + field.getName() + "' in " + field.getDeclaringClass().getName() +
                                " is annotated with @SkeletonItemProps but is not of type Item.Properties."
                );
            }

        } catch (IllegalAccessException e) {
            throw new SkeletonReflectionException("Could not access @SkeletonItemProps field: " + field.getName(), e);
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