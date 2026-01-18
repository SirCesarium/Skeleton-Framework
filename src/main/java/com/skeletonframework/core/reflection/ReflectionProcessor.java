package com.skeletonframework.core.reflection;

import com.skeletonframework.core.reflection.base.ReflectionVisitor;
import com.skeletonframework.core.reflection.base.ReflectiveResolver;
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
