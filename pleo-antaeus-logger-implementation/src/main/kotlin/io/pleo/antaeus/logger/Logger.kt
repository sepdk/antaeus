package io.pleo.antaeus.logger.Implementation

import io.pleo.antaeus.logger.ILogger
import mu.KLogger

class Logger(private val logger: KLogger) : ILogger {
    override fun error(error: String) {
       logger.error(error)
    }

    override fun warn(warning: String) {
        logger.warn(warning)
    }
}