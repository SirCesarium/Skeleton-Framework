package com.skeletonframework.core.annotation.item;

import com.skeletonframework.core.annotation.SkeletonAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SkeletonAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SkeletonItem {
    String value();
}
