package io.github.sircesarium.skeletonframework.core.visitor;

import io.github.sircesarium.skeletonframework.core.error.SkeletonReflectionException;
import io.github.sircesarium.skeletonframework.core.reflection.base.ReflectionVisitor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class SkeletonBlockVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister.Blocks blockRegister;
    private final DeferredRegister.Items itemRegister;

    public SkeletonBlockVisitor(IEventBus eventBus, ModContainer container) {
        this.blockRegister = DeferredRegister.createBlocks(container.getModId());
        this.itemRegister = DeferredRegister.createItems(container.getModId());

        this.blockRegister.register(eventBus);
        this.itemRegister.register(eventBus);
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        String blockName = (String) data.annotationData().get("value");
        boolean withItem = (boolean) data.annotationData().getOrDefault("withItem", true);

        validateField(field);

        try {
            Object value = field.get(null);
            Supplier<Block> blockSupplier;

            if (value instanceof Supplier<?> sup) {
                blockSupplier = () -> castBlock(sup.get(), field);
            } else if (Block.class.isAssignableFrom(field.getType())) {
                blockSupplier = () -> {
                    try {
                        Object v = field.get(null);
                        if (v == null) {
                            Block instance = createInstance(field);
                            field.set(null, instance);
                            return instance;
                        }
                        return (Block) v;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to auto-instantiate block field: " + field.getName(), e);
                    }
                };
            } else {
                throw new SkeletonReflectionException(
                        "@SkeletonBlock field must be Block or Supplier<Block>: "
                                + field.getDeclaringClass().getName()
                                + "#" + field.getName()
                );
            }

            register(blockName, withItem, blockSupplier);

        } catch (IllegalAccessException e) {
            throw new SkeletonReflectionException(
                    "Cannot access @SkeletonBlock field: " + field.getName(), e
            );
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new SkeletonReflectionException(
                    "@SkeletonBlock field must be static: "
                            + field.getDeclaringClass().getName()
                            + "#" + field.getName()
            );
        }
    }

    private void register(String name, boolean withItem, Supplier<Block> blockSupplier) {
        var blockRegistryObject =
                blockRegister.register(name, blockSupplier);

        if (withItem) {
            itemRegister.registerSimpleBlockItem(
                    name,
                    blockRegistryObject
            );
        }
    }

    private Block castBlock(Object value, Field field) {
        if (!(value instanceof Block block)) {
            throw new SkeletonReflectionException(
                    "Supplier for @SkeletonBlock must supply Block: "
                            + field.getDeclaringClass().getName()
                            + "#" + field.getName()
            );
        }
        return block;
    }

    @SuppressWarnings("unused")
    private Block createInstance(Field field) {
        // TODO: Check if annotated with @WithBlockProps
        // if (field.isAnnotationPresent(WithBlockProps.class)) { ... }

        return new Block(BlockBehaviour.Properties.of());
    }
}
