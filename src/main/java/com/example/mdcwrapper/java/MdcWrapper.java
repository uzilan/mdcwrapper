package com.example.mdcwrapper.java;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MdcWrapper implements AutoCloseable {

    private final Logger logger;
    private final Level level;
    private final String action;
    private final List<String> keys;

    private MdcWrapper(final Logger logger, final Level level, final String action, Map<String, String> entries) {
        this.logger = logger;
        this.level = level;
        this.action = action;
        this.keys = new ArrayList<>();
        entries.forEach((key, value) -> {
            MDC.put(key, value);
            keys.add(key);
        });
        log("Starting {}");
    }

    public MdcWrapper put(final String key, final String value) {
        MDC.put(key, value);
        keys.add(key);
        return this;
    }

    @Override
    public void close() {
        log("Finished {}");
        keys.forEach(MDC::remove);
    }

    private void log(final String format) {
        if (level == Level.INFO) {
            logger.info(format, action);
        } else {
            logger.debug(format, action);
        }
    }

    public static MdcWrapper info(final Logger logger, final String action) {
        return info(logger, action, Map.of());
    }

    public static MdcWrapper info(final Logger logger, final String action, Map<String, String> entries) {
        return new MdcWrapper(logger, Level.INFO, action, entries);
    }

    public static MdcWrapper debug(final Logger logger, final String action) {
        return debug(logger, action, Map.of());
    }

    public static MdcWrapper debug(final Logger logger, final String action, Map<String, String> entries) {
        return new MdcWrapper(logger, Level.DEBUG, action, entries);
    }
}

