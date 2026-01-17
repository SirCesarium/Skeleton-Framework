package com.skeletonframework.core.reflection.resolver;

import com.skeletonframework.core.reflection.base.ReflectiveResolver;
import com.skeletonframework.core.reflection.base.AbstractResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

public final class ClassResolver extends AbstractResolver
        implements ReflectiveResolver<Class<?>> {

    @Override
    public Class<?> resolve(ModFileScanData.AnnotationData data) {
        return resolveClass(data);
    }
}
