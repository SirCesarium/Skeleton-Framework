package io.github.sircesarium.skeletonframework.core.reflection.base;

import io.github.sircesarium.skeletonframework.core.error.SkeletonReflectionException;
import net.neoforged.neoforgespi.language.ModFileScanData;

public abstract class AbstractResolver {

    protected Class<?> resolveClass(ModFileScanData.AnnotationData data) {
        try {
            return Class.forName(data.clazz().getClassName());
        } catch (ClassNotFoundException e) {
            throw new SkeletonReflectionException(
                    "Class not found: " + data.clazz().getClassName(), e
            );
        }
    }
}
