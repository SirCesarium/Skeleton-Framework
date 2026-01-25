package io.github.sircesarium.beaconcore.core.visitor.block;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.reflection.base.BlockWithItem;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.beaconcore.core.registry.RegistryManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class BlockRegistryVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister<Block> blockRegister;
    private final DeferredRegister.Items itemRegister;

    public BlockRegistryVisitor(IEventBus eventBus, ModContainer container) {
        this.blockRegister = RegistryManager.getOrCreate(eventBus, container.getModId(), Registries.BLOCK);
        this.itemRegister = (DeferredRegister.Items) RegistryManager.getOrCreate(eventBus, container.getModId(), Registries.ITEM);
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        String blockName = (String) data.annotationData().get("value");

        validateField(field);

        Class<?> type = field.getType();
        boolean isCustomComposite = type == BlockWithItem.CustomBlock.class;
        boolean isSimpleComposite = type == BlockWithItem.class;
        boolean isBlock = Block.class.isAssignableFrom(type);

        if (!isCustomComposite && !isSimpleComposite && !isBlock) {
            throw new BeaconReflectionException("@RegisterBlock field must be Block, BlockWithItem or CustomBlock: " + field.getName());
        }

        Class<? extends Block> blockClass = resolveBlockClass(field, isCustomComposite, isSimpleComposite);

        Supplier<Block> blockSupplier = () -> createInstance(field, blockClass);

        var blockHolder = blockRegister.register(blockName, blockSupplier);if (isCustomComposite || isSimpleComposite) {
            var itemHolder = itemRegister.registerSimpleBlockItem(blockName, blockHolder);
            applyCompositeValue(field, blockHolder, itemHolder, isCustomComposite);
        }
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

    @SuppressWarnings("unchecked")
    private Class<? extends Block> resolveBlockClass(Field field, boolean isCustom, boolean isSimple) {
        if (isSimple || !isCustom) {
            return (Class<? extends Block>) (isSimple ? Block.class : field.getType());
        }

        java.lang.reflect.Type genericType = field.getGenericType();
        if (genericType instanceof java.lang.reflect.ParameterizedType pt) {
            return (Class<? extends Block>) pt.getActualTypeArguments()[0];
        }

        return Block.class;
    }

    @SuppressWarnings("unchecked")
    private void applyCompositeValue(Field field, DeferredHolder<Block, ?> bH, DeferredHolder<Item, ?> iH, boolean isCustom) {
        try {
            Object composite = isCustom
                    ? new BlockWithItem.CustomBlock<>(bH, iH)
                    : new BlockWithItem((DeferredHolder<Block, Block>) bH, iH);

            field.set(null, composite);
        } catch (IllegalAccessException e) {
            throw new BeaconReflectionException("Failed to set Block composite value", e);
        }
    }

    @SuppressWarnings("unused")
    private Block createInstance(Field field, Class<? extends Block> blockClass) {
        // TODO: Check if annotated with @WithBlockProps
        // if (field.isAnnotationPresent(WithBlockProps.class)) { ... }

        try {
            var constructor = blockClass.getDeclaredConstructor(BlockBehaviour.Properties.class);
            constructor.setAccessible(true);

            return constructor.newInstance(BlockBehaviour.Properties.of());
        } catch (Exception e) {
            throw new BeaconReflectionException("Could not instantiate " + blockClass.getName() +
                    ". Ensure it has a constructor for BlockBehaviour.Properties", e);
        }
    }
}
