package io.github.sircesarium.beaconcore.core;

import io.github.sircesarium.beaconcore.BeaconCore;
import io.github.sircesarium.beaconcore.core.annotation.block.RegisterBlock;
import io.github.sircesarium.beaconcore.core.annotation.generic.BeaconMod;
import io.github.sircesarium.beaconcore.core.annotation.item.RegisterItem;
import io.github.sircesarium.beaconcore.core.annotation.item.RegisterItemProps;
import io.github.sircesarium.beaconcore.core.reflection.ReflectionProcessor;
import io.github.sircesarium.beaconcore.core.reflection.resolver.FieldResolver;
import io.github.sircesarium.beaconcore.core.visitor.block.BlockRegistryVisitor;
import io.github.sircesarium.beaconcore.core.visitor.item.ItemPropsRegistryVisitor;
import io.github.sircesarium.beaconcore.core.visitor.item.ItemRegistryVisitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

public class BeaconBootstrapper {
    public static void bootstrap(IEventBus eventBus, ModContainer container) {
        AnnotationIndex index = new AnnotationIndex(container);

        boolean shouldSkip = index.find(BeaconMod.class, ElementType.TYPE).findAny().isEmpty();

        if (shouldSkip) return;

        FieldResolver fieldResolver = new FieldResolver();

        ReflectionProcessor<Field> processor = new ReflectionProcessor<>(fieldResolver);

        processor.process(
                index.find(RegisterItemProps.class, ElementType.FIELD),
                new ItemPropsRegistryVisitor(container)
        );

        processor.process(
                index.find(RegisterItem.class, ElementType.FIELD),
                new ItemRegistryVisitor(eventBus, container)
        );

        processor.process(
                index.find(RegisterBlock.class, ElementType.FIELD),
                new BlockRegistryVisitor(eventBus, container)
        );
    }

    public static void bootstrapAll(IEventBus eventBus) {
        for (ModContainer container : ModList.get().getSortedMods()) {
            try {
                bootstrap(eventBus, container);
            } catch (Exception e) {
                BeaconCore.LOGGER.error(
                        "BeaconCore failed processing mod '{}'",
                        container.getModId(),
                        e
                );

                throw e;
            }
        }
    }
}
