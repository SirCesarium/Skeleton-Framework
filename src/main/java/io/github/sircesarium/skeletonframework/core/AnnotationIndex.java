package io.github.sircesarium.skeletonframework.core;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.objectweb.asm.Type;

public final class AnnotationIndex {

    private final Map<Type, List<ModFileScanData.AnnotationData>> byAnnotation;

    public AnnotationIndex(ModContainer container) {
        var scan = container.getModInfo()
                .getOwningFile()
                .getFile()
                .getScanResult();

        Map<Type, List<ModFileScanData.AnnotationData>> annotationMap = new HashMap<>();

        scan.getAnnotations().forEach(data ->
                annotationMap
                        .computeIfAbsent(data.annotationType(), k -> new ArrayList<>())
                        .add(data)
        );

        this.byAnnotation = Map.copyOf(annotationMap);
    }

    public Stream<ModFileScanData.AnnotationData> find(
            Class<? extends Annotation> annotation,
            ElementType target
    ) {
        Type asmType = Type.getType(annotation);

        return byAnnotation
                .getOrDefault(asmType, List.of())
                .stream()
                .filter(a -> a.targetType() == target);
    }
}
