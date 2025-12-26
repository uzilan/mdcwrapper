package com.example.mdcwrapper.kotlin

import org.slf4j.Logger
import org.slf4j.MDC
import org.slf4j.event.Level

class MdcWrapper private constructor(
    val logger: Logger,
    val level: Level,
    val action: String,
    vararg entries: Pair<String, String>,
) : AutoCloseable {
    private val keys: MutableList<String> = ArrayList<String>()

    init {
        entries.forEach {
            val (key, value) = it
            MDC.put(key, value)
            keys.add(key)
        }
        log("Started {}")
    }

    fun put(
        key: String,
        value: String,
    ): MdcWrapper {
        MDC.put(key, value)
        keys.add(key)
        return this
    }

    override fun close() {
        log("Finished {}")
        keys.forEach(MDC::remove)
    }

    private fun log(format: String) {
        if (level == Level.INFO) {
            logger.info(format, action)
        } else {
            logger.debug(format, action)
        }
    }

    companion object {
        fun info(
            logger: Logger,
            action: String,
            vararg entries: Pair<String, String>,
        ): MdcWrapper = MdcWrapper(logger, Level.INFO, action, *entries)

        fun debug(
            logger: Logger,
            action: String,
            vararg entries: Pair<String, String>,
        ): MdcWrapper = MdcWrapper(logger, Level.DEBUG, action, *entries)
    }
}
