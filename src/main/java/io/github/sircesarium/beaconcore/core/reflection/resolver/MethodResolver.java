package io.github.sircesarium.beaconcore.core.reflection.resolver;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectiveResolver;
import io.github.sircesarium.beaconcore.core.reflection.base.AbstractResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("unused")
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
            throw new BeaconReflectionException(
                    "Method not found: " + data.memberName(), e
            );
        }
    }
}

