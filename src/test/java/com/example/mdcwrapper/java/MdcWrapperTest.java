package com.example.mdcwrapper.java;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Java MdcWrapper.
 */
public final class MdcWrapperTest {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(MdcWrapperTest.class);
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

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

    @Test
    public void shouldSupportNestedWrappers() {
        try (final MdcWrapper outer = MdcWrapper.info(logger, "outer action", Map.of("outerKey", "outerValue", "sharedKey", "sharedValue"))) {

            assertThat(MDC.get("outerKey")).isEqualTo("outerValue");
            assertThat(MDC.get("sharedKey")).isEqualTo("sharedValue");
            logger.info("Message in outer wrapper");

            try (final MdcWrapper inner = MdcWrapper.debug(logger, "inner action", Map.of("innerKey", "innerValue"))) {

                assertThat(MDC.get("outerKey")).isEqualTo("outerValue");
                assertThat(MDC.get("sharedKey")).isEqualTo("sharedValue");
                assertThat(MDC.get("innerKey")).isEqualTo("innerValue");
                logger.debug("Message in inner wrapper");
            }

            // After inner close, innerKey should be gone, outerKey should remain
            assertThat(MDC.get("innerKey")).isNull();
            assertThat(MDC.get("outerKey")).isEqualTo("outerValue");
            assertThat(MDC.get("sharedKey")).isEqualTo("sharedValue");
        }

        // After outer close, both should be gone
        assertThat(MDC.get("outerKey")).isNull();
        assertThat(MDC.get("sharedKey")).isNull();
        assertThat(MDC.get("innerKey")).isNull();

        // Verify logs
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).hasSize(6); // Started outer, Msg outer, Started inner, Msg inner, Finished inner, Finished outer

        assertThat(logs.get(0).getFormattedMessage()).isEqualTo("Started outer action");
        assertThat(logs.get(0).getMDCPropertyMap()).containsEntry("outerKey", "outerValue");
        assertThat(logs.get(0).getMDCPropertyMap()).containsEntry("sharedKey", "sharedValue");

        assertThat(logs.get(1).getFormattedMessage()).isEqualTo("Message in outer wrapper");
        assertThat(logs.get(1).getMDCPropertyMap()).containsEntry("outerKey", "outerValue");

        assertThat(logs.get(2).getFormattedMessage()).isEqualTo("Started inner action");
        assertThat(logs.get(2).getMDCPropertyMap()).containsEntry("innerKey", "innerValue");
        assertThat(logs.get(2).getMDCPropertyMap()).containsEntry("outerKey", "outerValue");

        assertThat(logs.get(3).getFormattedMessage()).isEqualTo("Message in inner wrapper");
        assertThat(logs.get(3).getMDCPropertyMap()).containsEntry("innerKey", "innerValue");
        assertThat(logs.get(3).getMDCPropertyMap()).containsEntry("outerKey", "outerValue");

        assertThat(logs.get(4).getFormattedMessage()).isEqualTo("Finished inner action");
        assertThat(logs.get(4).getMDCPropertyMap()).containsEntry("innerKey", "innerValue");
        assertThat(logs.get(4).getMDCPropertyMap()).containsEntry("outerKey", "outerValue");

        assertThat(logs.get(5).getFormattedMessage()).isEqualTo("Finished outer action");
        assertThat(logs.get(5).getMDCPropertyMap()).containsEntry("outerKey", "outerValue");
        assertThat(logs.get(5).getMDCPropertyMap()).doesNotContainKey("innerKey");
    }
}

