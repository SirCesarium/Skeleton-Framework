package io.github.sircesarium.beaconcore.core.error;

public class BeaconReflectionException extends RuntimeException {
    public BeaconReflectionException(String message) {
        super(message);
    }

    public BeaconReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
