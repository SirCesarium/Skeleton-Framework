package io.github.sircesarium.skeletonframework.core.annotation.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WithItemProperties {
    String value() default ""; // TODO: replace with %item_field_name%_PROPS
}

