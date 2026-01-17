package com.skeletonframework.core.reflection.base;

import net.neoforged.neoforgespi.language.ModFileScanData;

public interface ReflectiveResolver<T> {
    T resolve(ModFileScanData.AnnotationData data);
}
