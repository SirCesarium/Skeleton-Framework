package io.github.sircesarium.beaconcore;

import io.github.sircesarium.beaconcore.core.BeaconBootstrapper;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(BeaconCore.MOD_ID)
public class BeaconCore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "beacon_core";

    public BeaconCore(IEventBus eventBus) {
        BeaconBootstrapper.bootstrapAll(eventBus);
    }
}
