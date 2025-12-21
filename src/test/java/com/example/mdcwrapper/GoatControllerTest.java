package com.example.mdcwrapper;

import com.example.mdcwrapper.java.Goat;
import com.example.mdcwrapper.java.GoatController;
import com.example.mdcwrapper.java.GoatNotFoundException;
import com.example.mdcwrapper.java.GoatRepository;
import com.example.mdcwrapper.java.GoatService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for GoatController.
 */
public final class GoatControllerTest {

    private static class MockGoatRepository implements GoatRepository {
        private long idCounter = 0L;
        private final Map<Long, Goat> goats = new HashMap<>();

        @Override
        public <S extends Goat> S save(final S entity) {
            final Goat goat = entity.id() == null ? entity.withId(++idCounter) : entity;
            goats.put(goat.id(), goat);
            return (S) goat;
        }

        @Override
        public <S extends Goat> Iterable<S> saveAll(final Iterable<S> entities) {
            return entities;
        }

        @Override
        public Optional<Goat> findById(final Long id) {
            return Optional.ofNullable(goats.get(id));
        }

        @Override
        public boolean existsById(final Long id) {
            return goats.containsKey(id);
        }

        @Override
        public Iterable<Goat> findAll() {
            return List.copyOf(goats.values());
        }

        @Override
        public Iterable<Goat> findAllById(final Iterable<Long> ids) {
            return List.of();
        }

        @Override
        public long count() {
            return goats.size();
        }

        @Override
        public void deleteById(final Long id) {
            goats.remove(id);
        }

        @Override
        public void delete(final Goat entity) {
            goats.remove(entity.id());
        }

        @Override
        public void deleteAllById(final Iterable<? extends Long> ids) {
            ids.forEach(goats::remove);
        }

        @Override
        public void deleteAll(final Iterable<? extends Goat> entities) {
            entities.forEach(entity -> goats.remove(entity.id()));
        }

        @Override
        public void deleteAll() {
            goats.clear();
        }
    }

    @Test
    public void shouldCreateAGoatWithGeneratedId() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);
        final Goat goat = new Goat(null, "Billy", "Alpine");

        final var response = controller.createGoat(goat);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().name()).isEqualTo("Billy");
        assertThat(response.getBody().breed()).isEqualTo("Alpine");
    }

    @Test
    public void shouldGetAllGoats() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);
        controller.createGoat(new Goat(null, "Goat 1", "Nubian"));
        controller.createGoat(new Goat(null, "Goat 2", "Boer"));

        final var response = controller.getAllGoats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    public void shouldGetGoatById() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);
        final var created = controller.createGoat(new Goat(null, "Test Goat", "Test Breed"));

        final var response = controller.getGoatById(created.getBody().id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Test Goat");
    }

    @Test
    public void shouldThrowExceptionWhenGoatDoesNotExist() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);

        assertThatThrownBy(() -> controller.getGoatById(999L))
            .isInstanceOf(GoatNotFoundException.class);
    }

    @Test
    public void shouldUpdateExistingGoat() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);
        final var created = controller.createGoat(new Goat(null, "Original", "Original Breed"));

        final var response = controller.updateGoat(
            created.getBody().id(),
            new Goat(null, "Updated", "Updated Breed")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Updated");
        assertThat(response.getBody().breed()).isEqualTo("Updated Breed");
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistentGoat() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);

        assertThatThrownBy(() ->
            controller.updateGoat(999L, new Goat(null, "Updated", "Updated Breed"))
        ).isInstanceOf(GoatNotFoundException.class);
    }

    @Test
    public void shouldDeleteExistingGoat() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);
        final var created = controller.createGoat(new Goat(null, "Test Goat", "Test Breed"));

        final var response = controller.deleteGoat(created.getBody().id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentGoat() {
        final MockGoatRepository repository = new MockGoatRepository();
        final GoatService service = new GoatService(repository);
        final GoatController controller = new GoatController(service);

        assertThatThrownBy(() -> controller.deleteGoat(999L))
            .isInstanceOf(GoatNotFoundException.class);
    }
}

