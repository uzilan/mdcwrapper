package com.example.mdcwrapper;

import com.example.mdcwrapper.java.MdcWrapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Java MdcWrapper.
 */
public final class MdcWrapperTest {

    private static final Logger logger = LoggerFactory.getLogger(MdcWrapperTest.class);

    @Test
    public void shouldCreateInfoWrapper() {
        final MdcWrapper wrapper = MdcWrapper.info(logger, "test action");

        assertThat(wrapper).isNotNull();
    }

    @Test
    public void shouldCreateDebugWrapper() {
        final MdcWrapper wrapper = MdcWrapper.debug(logger, "debug action");

        assertThat(wrapper).isNotNull();
    }

    @Test
    public void shouldPutAndRemoveMdcKeys() {
        try (final MdcWrapper wrapper = MdcWrapper.info(logger, "test")) {
            wrapper.put("key1", "value1")
                   .put("key2", "value2");

            assertThat(MDC.get("key1")).isEqualTo("value1");
            assertThat(MDC.get("key2")).isEqualTo("value2");
        }

        // Keys should be removed after close
        assertThat(MDC.get("key1")).isNull();
        assertThat(MDC.get("key2")).isNull();
    }

    @Test
    public void shouldSupportFluentApi() {
        try (final MdcWrapper wrapper = MdcWrapper.info(logger, "fluent test")
                .put("user", "john")
                .put("requestId", "123")) {

            assertThat(MDC.get("user")).isEqualTo("john");
            assertThat(MDC.get("requestId")).isEqualTo("123");
        }

        assertThat(MDC.get("user")).isNull();
        assertThat(MDC.get("requestId")).isNull();
    }
}

