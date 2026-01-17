package com.skeletonframework.core.reflection;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.stream.Stream;

public final class AnnotationScanHelper {

    private final ModContainer container;

    public AnnotationScanHelper(ModContainer container) {
        this.container = container;
    }

    public Stream<ModFileScanData.AnnotationData> find(
            Class<? extends Annotation> annotation,
            ElementType target
    ) {
        return container.getModInfo()
                .getOwningFile()
                .getFile()
                .getScanResult()
                .getAnnotatedBy(annotation, target);
    }
}
