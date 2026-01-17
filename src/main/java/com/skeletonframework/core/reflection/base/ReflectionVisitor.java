package com.skeletonframework.core.reflection.base;

import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ReflectionVisitor {

    void visit(Field field, ModFileScanData.AnnotationData data);

    void visit(Method method, ModFileScanData.AnnotationData data);

    void visit(Class<?> clazz, ModFileScanData.AnnotationData data);
}
