package io.github.sircesarium.beaconcore.core.reflection.base;

import java.util.function.Supplier;

public final class BeaconHolder<T> implements Supplier<T> {
    private final Supplier<T> factory;
    private volatile T value;

    public BeaconHolder(Supplier<T> factory) {
        this.factory = factory;
    }

    @Override
    public T get() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    value = result = factory.get();
                }
            }
        }
        return result;
    }
}