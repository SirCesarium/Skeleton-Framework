package io.github.sircesarium.beaconcore.core.annotation.item;

import net.minecraft.world.item.Item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterItem {
    String value();
    Class<? extends Item> type() default Item.class;
}
