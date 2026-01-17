package com.skeletonframework.core.reflection;

import com.skeletonframework.core.reflection.base.ReflectionVisitor;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public final class ReflectionProcessor {

    private final ReflectionResolverRegistry registry;

    public ReflectionProcessor() {
        this.registry = new ReflectionResolverRegistry();
    }

    public void process(
            Stream<ModFileScanData.AnnotationData> annotations,
            ReflectionVisitor visitor
    ) {
        annotations.forEach(data -> {
            Object element = registry.resolve(data);

            if (element instanceof Field f) visitor.visit(f, data);
            else if (element instanceof Method m) visitor.visit(m, data);
            else if (element instanceof Class<?> c) visitor.visit(c, data);
        });
    }
}
