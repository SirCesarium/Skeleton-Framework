package io.github.sircesarium.beaconcore.core.reflection.resolver;

import io.github.sircesarium.beaconcore.core.reflection.base.ReflectiveResolver;
import io.github.sircesarium.beaconcore.core.reflection.base.AbstractResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

@SuppressWarnings("unused")
public final class ClassResolver extends AbstractResolver
        implements ReflectiveResolver<Class<?>> {

    @Override
    public Class<?> resolve(ModFileScanData.AnnotationData data) {
        return resolveClass(data);
    }
}
