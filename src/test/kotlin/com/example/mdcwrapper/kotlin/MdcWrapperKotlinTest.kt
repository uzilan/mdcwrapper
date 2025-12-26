package com.example.kotlin.mdcwrapper

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.example.mdcwrapper.kotlin.MdcWrapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class MdcWrapperKotlinTest {
    private val logger = LoggerFactory.getLogger(MdcWrapperKotlinTest::class.java) as Logger
    private val listAppender = ListAppender<ILoggingEvent>()

    @BeforeEach
    fun setup() {
        listAppender.start()
        logger.addAppender(listAppender)
    }

    @AfterEach
    fun tearDown() {
        logger.detachAppender(listAppender)
    }

    @Test
    fun `should support nested wrappers`() {
        MdcWrapper.info(logger, "outer action", "outerKey" to "outerValue", "sharedKey" to "sharedValue").use { outer ->
            assertThat(MDC.get("outerKey")).isEqualTo("outerValue")
            assertThat(MDC.get("sharedKey")).isEqualTo("sharedValue")
            logger.info("Message in outer wrapper")

            MdcWrapper.debug(logger, "inner action", "innerKey" to "innerValue").use { inner ->
                assertThat(MDC.get("outerKey")).isEqualTo("outerValue")
                assertThat(MDC.get("sharedKey")).isEqualTo("sharedValue")
                assertThat(MDC.get("innerKey")).isEqualTo("innerValue")
                logger.debug("Message in inner wrapper")
            }

            // After inner close, innerKey should be gone, outerKey should remain
            assertThat(MDC.get("innerKey")).isNull()
            assertThat(MDC.get("outerKey")).isEqualTo("outerValue")
            assertThat(MDC.get("sharedKey")).isEqualTo("sharedValue")
        }

        // After outer close, both should be gone
        assertThat(MDC.get("outerKey")).isNull()
        assertThat(MDC.get("sharedKey")).isNull()
        assertThat(MDC.get("innerKey")).isNull()

        // Verify logs
        val logs = listAppender.list
        assertThat(logs).hasSize(6) // Started outer, Msg outer, Started inner, Msg inner, Finished inner, Finished outer

        assertThat(logs[0].formattedMessage).isEqualTo("Started outer action")
        assertThat(logs[0].mdcPropertyMap).containsEntry("outerKey", "outerValue")
        assertThat(logs[0].mdcPropertyMap).containsEntry("sharedKey", "sharedValue")

        assertThat(logs[1].formattedMessage).isEqualTo("Message in outer wrapper")
        assertThat(logs[1].mdcPropertyMap).containsEntry("outerKey", "outerValue")

        assertThat(logs[2].formattedMessage).isEqualTo("Started inner action")
        assertThat(logs[2].mdcPropertyMap).containsEntry("innerKey", "innerValue")
        assertThat(logs[2].mdcPropertyMap).containsEntry("outerKey", "outerValue")

        assertThat(logs[3].formattedMessage).isEqualTo("Message in inner wrapper")
        assertThat(logs[3].mdcPropertyMap).containsEntry("innerKey", "innerValue")
        assertThat(logs[3].mdcPropertyMap).containsEntry("outerKey", "outerValue")

        assertThat(logs[4].formattedMessage).isEqualTo("Finished inner action")
        assertThat(logs[4].mdcPropertyMap).containsEntry("innerKey", "innerValue")
        assertThat(logs[4].mdcPropertyMap).containsEntry("outerKey", "outerValue")

        assertThat(logs[5].formattedMessage).isEqualTo("Finished outer action")
        assertThat(logs[5].mdcPropertyMap).containsEntry("outerKey", "outerValue")
        assertThat(logs[5].mdcPropertyMap).doesNotContainKey("innerKey")
    }
}
