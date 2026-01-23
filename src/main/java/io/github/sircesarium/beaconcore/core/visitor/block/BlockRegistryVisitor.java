package io.github.sircesarium.beaconcore.core.visitor.block;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class BlockRegistryVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister.Blocks blockRegister;
    private final DeferredRegister.Items itemRegister;

    public BlockRegistryVisitor(IEventBus eventBus, ModContainer container) {
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

        Supplier<Block> blockSupplier;

        if (Block.class.isAssignableFrom(field.getType())) {
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
            throw new BeaconReflectionException(
                    "@RegisterBlock field must be Block: "
                            + field.getDeclaringClass().getName()
                            + "#" + field.getName()
            );
        }

        register(blockName, withItem, blockSupplier);
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new BeaconReflectionException(
                    "@RegisterBlock field must be static: "
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

    @SuppressWarnings("unused")
    private Block createInstance(Field field) {
        // TODO: Check if annotated with @WithBlockProps
        // if (field.isAnnotationPresent(WithBlockProps.class)) { ... }

        return new Block(BlockBehaviour.Properties.of());
    }
}
