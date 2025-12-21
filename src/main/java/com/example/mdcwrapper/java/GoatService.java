package com.example.mdcwrapper.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Service for managing goats.
 */
@Service
public final class GoatService {

    private final GoatRepository goatRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public GoatService(final GoatRepository goatRepository) {
        this.goatRepository = goatRepository;
    }

    public Goat createGoatTheOldWay(final Goat goat) {
        try {
            MDC.put("name", goat.name());
            logger.info("adding a new goat.");
            final Goat saved = goatRepository.save(goat);
            MDC.put("id", "" + saved.id());
            logger.info("added a new goat.");
            return saved;
        } finally {
            MDC.clear();
        }
    }

    public Goat createGoat(final Goat goat) {
        try (final MdcWrapper mdc = MdcWrapper.debug(logger, "creating a new goat", Map.of("name", goat.name()))) {
            final Goat saved = goatRepository.save(goat);
            mdc.put("id", "" + saved.id());
            return saved;
        }
    }

    public List<Goat> getAllGoats() {
        try (final MdcWrapper mdc = MdcWrapper.debug(logger, "fetching all goats")) {
            final List<Goat> goats = StreamSupport.stream(goatRepository.findAll().spliterator(), false).toList();
            mdc.put("goats", "" + goats.size());
            return goats;
        }
    }

    public Goat getGoatById(final Long id) {
        try (final MdcWrapper ignored = MdcWrapper.debug(logger, "getting a goat")) {
            return goatRepository.findById(id)
                    .orElseThrow(GoatNotFoundException::new);
        }
    }

    public Goat updateGoat(final Long id, final Goat goat) {
        try (final MdcWrapper ignored = MdcWrapper.debug(logger, "updating a goat")) {
            return goatRepository.findById(id)
                    .map(existing -> goatRepository.save(goat.withId(id)))
                    .orElseThrow(GoatNotFoundException::new);
        }
    }

    public void deleteGoat(final Long id) {
        try (final MdcWrapper ignored = MdcWrapper.debug(logger, "deleting a goat")) {
            goatRepository.findById(id)
                    .map(goat -> {
                        goatRepository.deleteById(id);
                        return goat;
                    })
                    .orElseThrow(GoatNotFoundException::new);
        }
    }
}

