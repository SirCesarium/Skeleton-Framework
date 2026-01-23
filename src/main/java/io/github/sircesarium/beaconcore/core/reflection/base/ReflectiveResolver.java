package io.github.sircesarium.beaconcore.core.reflection.base;

import net.neoforged.neoforgespi.language.ModFileScanData;

public interface ReflectiveResolver<T> {
    T resolve(ModFileScanData.AnnotationData data);
}
