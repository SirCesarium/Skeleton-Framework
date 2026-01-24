package io.github.sircesarium.beaconcore.core.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.HashMap;
import java.util.Map;

public final class RegistryManager {
    private static final Map<String, Map<ResourceKey<?>, DeferredRegister<?>>> REGISTERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> DeferredRegister<T> getOrCreate(IEventBus bus, String modId, ResourceKey<? extends Registry<T>> registryKey) {
        var modMap = REGISTERS.computeIfAbsent(modId, k -> new HashMap<>());

        return (DeferredRegister<T>) modMap.computeIfAbsent(registryKey, k -> {
            DeferredRegister<?> dr;

            if (registryKey.equals(Registries.ITEM)) {
                dr = DeferredRegister.createItems(modId);
            } else if (registryKey.equals(Registries.BLOCK)) {
                dr = DeferredRegister.createBlocks(modId);
            } else {
                dr = DeferredRegister.create(registryKey, modId);
            }

            dr.register(bus);
            return dr;
        });
    }
}
