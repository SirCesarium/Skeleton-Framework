package io.github.sircesarium.beaconcore.core.visitor.block;

import io.github.sircesarium.beaconcore.core.error.BeaconReflectionException;
import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
import io.github.sircesarium.beaconcore.core.reflection.base.ReflectionVisitor;
import io.github.sircesarium.beaconcore.core.registry.PropertyRegistry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public final class BlockPropsRegistryVisitor implements ReflectionVisitor<Field> {
    private final String modNamespace;

    public BlockPropsRegistryVisitor(ModContainer container) {
        this.modNamespace = container.getModId();
    }

    @Override
    public void visit(Field field, ModFileScanData.AnnotationData data) {
        validateField(field);

        String propsId = (String) data.annotationData().getOrDefault("value", "");
        if (propsId.isEmpty()) propsId = field.getName();

        String namespace = (String) data.annotationData().getOrDefault("namespace", modNamespace);
        String fullId = namespace + ":" + propsId;

        try {
            Object value = field.get(null);

            if (value instanceof BlockBehaviour.Properties props) {
                PropertyRegistry.saveBlockProps(fullId, () -> props);
            } else if (value instanceof Supplier<?> supplier) {
                PropertyRegistry.saveBlockProps(fullId, () -> {
                    Object supplied = supplier.get();
                    if (!(supplied instanceof BlockBehaviour.Properties p)) {
                        throw new BeaconRegistryException("Supplier field '" + field.getName() + "' must return BlockBehaviour.Properties");
                    }
                    return p;
                });
            } else {
                throw new BeaconRegistryException(
                        "Field '" + field.getName() + "' in " + field.getDeclaringClass().getSimpleName() +
                                " must be BlockBehaviour.Properties or Supplier<BlockBehaviour.Properties>"
                );
            }

        } catch (IllegalAccessException e) {
            throw new BeaconReflectionException("Failed to access static field for block properties: " + field.getName(), e);
        }
    }

    private void validateField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new BeaconRegistryException(
                    "@RegisterBlockProps field must be static: " +
                            field.getDeclaringClass().getName() + "#" + field.getName()
            );
        }
    }
}