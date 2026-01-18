package io.github.sircesarium.skeletonframework.core.reflection;

import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectiveResolver;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.util.stream.Stream;

public final class ReflectionProcessor<T> {

    private final ReflectiveResolver<T> resolver;

    public ReflectionProcessor(ReflectiveResolver<T> resolver) {
        this.resolver = resolver;
    }

    public void process(
            Stream<ModFileScanData.AnnotationData> annotations,
            ReflectionVisitor<T> visitor
    ) {
        annotations.forEach(data -> visitor.visit(resolver.resolve(data), data));
    }
}
