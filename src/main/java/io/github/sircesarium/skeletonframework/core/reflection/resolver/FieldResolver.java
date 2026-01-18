package io.github.sircesarium.skeletonframework.core.reflection.resolver;

import io.github.sircesarium.skeletonframework.core.error.SkeletonReflectionException;
import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectiveResolver;
import io.github.sircesarium.skeletonframework.core.reflection.base.AbstractResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;

public final class FieldResolver extends AbstractResolver
        implements ReflectiveResolver<Field> {

    @Override
    public Field resolve(ModFileScanData.AnnotationData data) {
        try {
            Class<?> clazz = resolveClass(data);
            Field field = clazz.getDeclaredField(data.memberName());
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new SkeletonReflectionException(
                    "Field not found: " + data.memberName(), e
            );
        }
    }
}
