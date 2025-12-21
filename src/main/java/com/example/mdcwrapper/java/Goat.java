package com.example.mdcwrapper.java;

import org.springframework.data.annotation.Id;

/**
 * Goat entity representing a goat in the system.
 */
public record Goat(@Id Long id, String name, String breed) {

    public Goat withId(final Long newId) {
        return new Goat(newId, this.name, this.breed);
    }
}

