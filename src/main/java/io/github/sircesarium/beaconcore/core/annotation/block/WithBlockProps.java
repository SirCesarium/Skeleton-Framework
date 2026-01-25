package io.github.sircesarium.beaconcore.core.annotation.block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithBlockProps {
    String value() default "";
    String fallback() default "";
}
