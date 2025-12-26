package com.example.mdcwrapper.java;

import com.example.mdcwrapper.MdcwrapperApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for GoatService.
 */
@SpringBootTest(classes = MdcwrapperApplication.class)
public final class GoatServiceIntegrationTest {

    @Autowired
    private GoatService goatService;

    @Autowired
    private GoatRepository goatRepository;

    @Test
    public void shouldAddGoatInDatabase() {
        final Goat goat = new Goat(null, "Billy", "Alpine");

        final Goat savedGoat = goatService.addGoat(goat);

        assertThat(savedGoat.id()).isNotNull();
        assertThat(savedGoat.name()).isEqualTo("Billy");
        assertThat(savedGoat.breed()).isEqualTo("Alpine");
    }

    @Test
    public void shouldGetAllGoatsFromDatabase() {
        goatRepository.deleteAll();
        goatService.addGoat(new Goat(null, "Goat 1", "Nubian"));
        goatService.addGoat(new Goat(null, "Goat 2", "Boer"));

        final var goats = goatService.getAllGoats();

        assertThat(goats).hasSize(2);
    }

    @Test
    public void shouldGetGoatByIdFromDatabase() {
        final Goat saved = goatService.addGoat(new Goat(null, "FindMe", "LaMancha"));

        final Goat found = goatService.getGoatById(saved.id());

        assertThat(found).isNotNull();
        assertThat(found.name()).isEqualTo("FindMe");
        assertThat(found.breed()).isEqualTo("LaMancha");
    }

    @Test
    public void shouldThrowExceptionWhenGoatNotFound() {
        assertThatThrownBy(() -> goatService.getGoatById(99999L))
                .isInstanceOf(GoatNotFoundException.class);
    }

    @Test
    public void shouldUpdateGoatInDatabase() {
        final Goat saved = goatService.addGoat(new Goat(null, "Original", "Original Breed"));

        final Goat updated = goatService.updateGoat(saved.id(), new Goat(null, "Updated", "Updated Breed"));

        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Updated");
        assertThat(updated.breed()).isEqualTo("Updated Breed");

        final Goat found = goatService.getGoatById(saved.id());
        assertThat(found.name()).isEqualTo("Updated");
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistentGoat() {
        assertThatThrownBy(() -> goatService.updateGoat(99999L, new Goat(null, "Updated", "Updated Breed")))
                .isInstanceOf(GoatNotFoundException.class);
    }

    @Test
    public void shouldDeleteGoatFromDatabase() {
        final Goat saved = goatService.addGoat(new Goat(null, "DeleteMe", "Delete Breed"));

        goatService.deleteGoat(saved.id());

        assertThatThrownBy(() -> goatService.getGoatById(saved.id()))
                .isInstanceOf(GoatNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentGoat() {
        assertThatThrownBy(() -> goatService.deleteGoat(99999L))
                .isInstanceOf(GoatNotFoundException.class);
    }

    @Test
    public void shouldUpdateGoatNameAndBreed() {
        // Given: A goat exists in the database
        final Goat originalGoat = new Goat(null, "Patches", "Nigerian Dwarf");
        final Goat savedGoat = goatService.addGoat(originalGoat);
        final Long goatId = savedGoat.id();

        // When: We update both name and breed
        final Goat updatedGoat = new Goat(null, "Spots", "Pygmy");
        final Goat result = goatService.updateGoat(goatId, updatedGoat);

        // Then: The goat should be updated with new values
        assertThat(result).isNotNull();
        assertThat(result.id()).as("ID should remain the same").isEqualTo(goatId);
        assertThat(result.name()).as("Name should be updated").isEqualTo("Spots");
        assertThat(result.breed()).as("Breed should be updated").isEqualTo("Pygmy");

        // And: The database should reflect the changes
        final Goat foundGoat = goatService.getGoatById(goatId);
        assertThat(foundGoat.name()).isEqualTo("Spots");
        assertThat(foundGoat.breed()).isEqualTo("Pygmy");
    }
}

