package com.skeletonframework.core.reflection.resolver;

import com.skeletonframework.core.error.SkeletonReflectionException;
import com.skeletonframework.core.reflection.base.ReflectiveResolver;
import com.skeletonframework.core.reflection.base.AbstractResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class MethodResolver extends AbstractResolver
        implements ReflectiveResolver<Method> {

    @Override
    public Method resolve(ModFileScanData.AnnotationData data) {
        try {
            Class<?> clazz = resolveClass(data);
            return Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().equals(data.memberName()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException(data.memberName()));
        } catch (NoSuchMethodException e) {
            throw new SkeletonReflectionException(
                    "Method not found: " + data.memberName(), e
            );
        }
    }
}

