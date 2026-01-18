package io.github.sircesarium.skeletonframework.core.reflection.base;

import net.neoforged.neoforgespi.language.ModFileScanData;

public interface ReflectionVisitor<T> {
    void visit(T element, ModFileScanData.AnnotationData data);
}

