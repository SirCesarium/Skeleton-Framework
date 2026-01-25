package io.github.sircesarium.beaconcore.core.visitor.block;

import io.github.sircesarium.beaconcore.core.annotation.block.WithBlockProps;
import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.reflection.base.BlockWithItem;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.beaconcore.core.registry.PropertyRegistry;
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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class BlockRegistryVisitor implements ReflectionVisitor<Field> {

    private final DeferredRegister<Block> blockRegister;
    private final DeferredRegister.Items itemRegister;
    private final String modNamespace;

    public BlockRegistryVisitor(IEventBus eventBus, ModContainer container) {
        this.modNamespace = container.getModId();
        this.blockRegister = RegistryManager.getOrCreate(eventBus, modNamespace, Registries.BLOCK);
        this.itemRegister = (DeferredRegister.Items) RegistryManager.getOrCreate(eventBus, modNamespace, Registries.ITEM);
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        String blockName = (String) data.annotationData().get("value");
        validateField(field);

        Class<?> type = field.getType();
        boolean isCustomComposite = type == BlockWithItem.CustomBlock.class;
        boolean isSimpleComposite = type == BlockWithItem.class;

        Class<? extends Block> blockClass = resolveBlockClass(field, isCustomComposite, isSimpleComposite);

        Supplier<Block> blockSupplier = getBlockSupplier(field, blockClass, isCustomComposite || isSimpleComposite);

        var blockHolder = blockRegister.register(blockName, blockSupplier);

        if (isCustomComposite || isSimpleComposite) {
            var itemHolder = itemRegister.registerSimpleBlockItem(blockName, blockHolder);
            applyCompositeValue(field, blockHolder, itemHolder, isCustomComposite);
        }
    }

    private @NotNull Supplier<Block> getBlockSupplier(Field field, Class<? extends Block> blockClass, boolean isComposite) {
        return () -> {
            try {
                Object v = field.get(null);

                if (v instanceof Supplier<?> supplier) {
                    Object supplied = supplier.get();
                    if (supplied instanceof Block b) return b;
                    if (supplied instanceof BlockWithItem composite) return composite.block();
                    throw new BeaconReflectionException("Supplier in field '" + field.getName() + "' did not return a Block or BlockWithItem");
                }

                if (v instanceof Block b) return b;

                if (v == null || isComposite) {
                    if (Modifier.isFinal(field.getModifiers()) && v == null) {
                        throw new BeaconReflectionException("Field '" + field.getName() + "' is final and null. Use BeaconHolder or remove final.");
                    }
                    return createInstance(field, blockClass);
                }

                throw new BeaconReflectionException("Unsupported field type for @RegisterBlock: " + field.getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access block field: " + field.getName(), e);
            }
        };
    }

    private Block createInstance(Field field, Class<? extends Block> blockClass) {
        try {
            BlockBehaviour.Properties props = null;
            WithBlockProps annotation = field.getAnnotation(WithBlockProps.class);

            if (annotation != null) {
                String propsId = annotation.value().isEmpty() ? field.getName() + "_PROPS" : annotation.value();
                String fullId = normalizeId(propsId, modNamespace);

                if (PropertyRegistry.existsInBlockProps(fullId)) {
                    props = PropertyRegistry.getBlockProps(fullId);
                } else if (!annotation.fallback().isEmpty()) {
                    String fallbackId = normalizeId(annotation.fallback(), modNamespace);
                    props = PropertyRegistry.getBlockProps(fallbackId);
                }
            }

            if (props == null) {
                props = BlockBehaviour.Properties.of();
            }

            Constructor<? extends Block> constructor = blockClass.getDeclaredConstructor(BlockBehaviour.Properties.class);
            constructor.setAccessible(true);
            return constructor.newInstance(props);

        } catch (Exception e) {
            throw new BeaconReflectionException("Failed to instantiate block " + blockClass.getName() + ". Check constructor.", e);
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new BeaconReflectionException("@RegisterBlock field must be static: " + field.getName());
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

    private static String normalizeId(String id, String defaultNamespace) {
        return id.contains(":") ? id : defaultNamespace + ":" + id;
    }
}