package io.github.sircesarium.skeletonframework.core.annotation.block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SkeletonBlock {
    String value();
    boolean withItem() default true;
}
