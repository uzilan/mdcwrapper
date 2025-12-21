package com.example.mdcwrapper.java;

/**
 * Exception thrown when a goat is not found.
 */
public final class GoatNotFoundException extends RuntimeException {

    public GoatNotFoundException() {
        super("Goat not found");
    }
}

