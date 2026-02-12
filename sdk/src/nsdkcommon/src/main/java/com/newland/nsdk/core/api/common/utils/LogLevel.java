package com.newland.nsdk.core.api.common.utils;

/**
 * Log level.
 *
* <ul> The order in terms of verbosity, from least to most is ERROR, WARN, INFO, DEBUG, VERBOSE.
*     <li>If debug level is {@link LogLevel#VERBOSE}, all types of logs will be output.</li>
*     <li>If debug level is {@link LogLevel#ERROR}, only error logs will be output.</li>
*     <li>If debug level is {@link LogLevel#OFF}, no log will be output.</li>
* </ul>
 */
public enum LogLevel {
    /**
     * Disable log output.
     */
    OFF,
    /**
     * Log level: Error.
     */
    ERROR,
    /**
     * Log level: Warn.
     */
    WARN,
    /**
     * Log level: Info.
     */
    INFO,
    /**
     * Log level: Debug.
     */
    DEBUG,
    /**
     * Log level: Verbose.
     */
    VERBOSE,
}
