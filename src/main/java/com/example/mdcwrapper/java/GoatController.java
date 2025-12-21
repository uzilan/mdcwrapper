package com.example.mdcwrapper.java;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing goats.
 */
@RestController
@RequestMapping("/api/goats")
public final class GoatController {

    private final GoatService goatService;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    public GoatController(final GoatService goatService) {
        this.goatService = goatService;
    }

    @PostMapping
    public ResponseEntity<Goat> createGoat(@RequestBody final Goat goat) {
        try (final var ignored = MdcWrapper.info(logger, "serving POST /api/goats")) {
            // final Goat createdGoat = goatService.createGoat(goat);
            final Goat createdGoat = goatService.createGoatTheOldWay(goat);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGoat);
        }
    }

    @GetMapping
    public ResponseEntity<List<Goat>> getAllGoats() {
        try (final var ignored = MdcWrapper.info(logger, "serving GET /api/goats")) {
            final List<Goat> goats = goatService.getAllGoats();
            return ResponseEntity.ok(goats);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goat> getGoatById(@PathVariable final Long id) {
        try (final var ignored = MdcWrapper.info(logger, "serving GET /api/goats/{id}", Map.of("id", id.toString()))) {
            final Goat goat = goatService.getGoatById(id);
            return ResponseEntity.ok(goat);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goat> updateGoat(@PathVariable final Long id, @RequestBody final Goat goat) {
        try (final var ignored = MdcWrapper.info(logger, "serving PUT /api/goats/{id}", Map.of("id", id.toString()))) {
            final Goat updatedGoat = goatService.updateGoat(id, goat);
            return ResponseEntity.ok(updatedGoat);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoat(@PathVariable final Long id) {
        try (final var ignored = MdcWrapper.info(logger, "serving DELETE /api/goats/{id}", Map.of("id", id.toString()))) {
            goatService.deleteGoat(id);
            return ResponseEntity.noContent().build();
        }
    }
}

