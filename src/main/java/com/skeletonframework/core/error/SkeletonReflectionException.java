package com.skeletonframework.core.error;

public class SkeletonReflectionException extends RuntimeException {
    public SkeletonReflectionException(String message) {
        super(message);
    }

    public SkeletonReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
