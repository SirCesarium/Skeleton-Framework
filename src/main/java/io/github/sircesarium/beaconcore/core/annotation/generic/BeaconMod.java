package io.github.sircesarium.beaconcore.core.annotation.generic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.sircesarium.beaconcore.core.annotation.block.RegisterBlock;
import io.github.sircesarium.beaconcore.core.annotation.item.RegisterItem;

/**
 * Marks a mod entrypoint class to be processed by BeaconCore.
 * Only classes annotated with {@link BeaconMod} will have their
 * {@link RegisterItem}, {@link RegisterBlock}, etc. automatically registered.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeaconMod {
}
