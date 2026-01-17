package com.skeletonframework.core.reflection;

import com.skeletonframework.core.error.SkeletonReflectionException;
import com.skeletonframework.core.reflection.base.ReflectiveResolver;
import com.skeletonframework.core.reflection.resolver.ClassResolver;
import com.skeletonframework.core.reflection.resolver.FieldResolver;
import com.skeletonframework.core.reflection.resolver.MethodResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.Map;

public final class ReflectionResolverRegistry {

    private final Map<ElementType, ReflectiveResolver<?>> resolvers;

    public ReflectionResolverRegistry() {
        this.resolvers = Map.of(
                ElementType.FIELD, new FieldResolver(),
                ElementType.METHOD, new MethodResolver(),
                ElementType.TYPE, new ClassResolver()
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(ModFileScanData.AnnotationData data) {
        ReflectiveResolver<?> resolver = resolvers.get(data.targetType());

        if (resolver == null) {
            throw new SkeletonReflectionException(
                    "No resolver registered for " + data.targetType()
            );
        }

        return (T) resolver.resolve(data);
    }
}
