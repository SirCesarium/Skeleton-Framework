package io.github.sircesarium.beaconcore.core.reflection.base;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import net.neoforged.neoforgespi.language.ModFileScanData;

public abstract class AbstractResolver {

    protected Class<?> resolveClass(ModFileScanData.AnnotationData data) {
        try {
            return Class.forName(
                    data.clazz().getClassName(),
                    false,
                    Thread.currentThread().getContextClassLoader()
            );
        } catch (ClassNotFoundException e) {
            throw new BeaconReflectionException(
                    "Class not found: " + data.clazz().getClassName(), e
            );
        }
    }
}
