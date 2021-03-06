/*
 * This file is part of CraftCommons.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
 * CraftCommons is licensed under the GNU Lesser General Public License.
 *
 * CraftCommons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CraftCommons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.commons.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingManager {
    private final Logger logger;
    private String prefix, directory, format = "HH:mm:ss";
    private boolean debug = false, logging = false, combined = true;
    protected static final Level debugLevel = new Level("DEBUG", Level.INFO.intValue() + 1) {
    };

    public LoggingManager(String logger, String prefix) {
        this.logger = Logger.getLogger(logger);
        this.prefix = prefix;
    }

    public static enum Type {
        info, error, debug, warning
    }

    public Logger getLogger() {
        return this.logger;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        if (directory == null || directory.isEmpty()) {
            this.logging = false;
        }
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isCombinedLogging() {
        return this.combined;
    }

    public void setCombinedLogging(boolean combined) {
        this.combined = combined;
    }

    public boolean isLogging() {
        return this.logging;
    }

    public void setLogging(boolean logging) {
        this.logging = (this.directory != null) && (!this.directory.isEmpty()) && logging;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void info(String line) {
        info(line, true);
    }

    public void info(String line, boolean logFile) {
        this.logger.info(this.prefix + " " + line);
        if (logFile) {
            toFile(Type.info, line);
        }
    }

    public void warning(String line) {
        warning(line, true);
    }

    public void warning(String line, boolean logFile) {
        this.logger.warning(this.prefix + " " + line);
        if (logFile) {
            toFile(Type.warning, line);
        }
    }

    public void severe(String line) {
        severe(line, true);
    }

    public void severe(String line, boolean logFile) {
        this.logger.severe(this.prefix + " " + line);
        if (logFile) {
            toFile(Type.error, line);
        }
    }

    public void debug(String line) {
        debug(line, true);
    }

    public void debug(String line, boolean logFile) {
        if (isDebug()) {
            this.logger.log(debugLevel, this.prefix + " " + line);
            if (logFile) {
                toFile(Type.debug, line);
            }
        }
    }

    public void error(String error) {
        severe(error);
    }

    public void advancedWarning() {
        warning(System.getProperty("line.separator")
                + "|-----------------------------------------------------------------------------|"
                + System.getProperty("line.separator")
                + "|---------------------------------- WARNING ----------------------------------|"
                + System.getProperty("line.separator")
                + "|-----------------------------------------------------------------------------|", false);
    }

    public void stackTrace(final Throwable e) {
        stackTrace(e, null);
    }

    public void stackTrace(final Throwable e, Map<Integer, String> extra) {
        advancedWarning();
        warning("Class name: " + e.getStackTrace()[1].getClassName(), false);
        warning("Error message: " + e.getMessage(), false);
        warning("Error cause: " + e.getCause(), false);
        warning("File name: " + e.getStackTrace()[1].getFileName(), false);
        warning("Function name: " + e.getStackTrace()[1].getMethodName(), false);
        warning("Error line: " + e.getStackTrace()[1].getLineNumber(), false);
        if (isLogging()) {
            DateFormat logFormat = new SimpleDateFormat(this.format);
            Date date = new Date();
            warning("Check log file: " + this.directory + "error" + File.separator
                    + logFormat.format(date) + "-error.log", false);
        } else {
            warning("Enable logging in the config to get more information about the error.", false);
        }

        logError("--------------------------- STACKTRACE ERROR ---------------------------");
        logError("Class name: " + e.getStackTrace()[1].getClassName());
        logError("Error message: " + e.getMessage());
        logError("Error cause: " + e.getCause());
        logError("File name: " + e.getStackTrace()[1].getFileName());
        logError("Function name: " + e.getStackTrace()[1].getMethodName());
        logError("Error line: " + e.getStackTrace()[1].getLineNumber());
        if (extra != null) {
            for (int id : extra.keySet()) {
                logError(extra.get(id));
            }
        }
        logError("--------------------------- STACKTRACE START ---------------------------");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            logError(e.getStackTrace()[i].toString());
        }
        logError("---------------------------- STACKTRACE END ----------------------------");
    }

    public void logError(String error) {
        toFile(Type.error, error);
    }

    private void toFile(Type type, String line) {
        if (this.logging) {
            File data = new File(this.directory, "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    debug("Created missing directory: " + this.directory);
                }
            }
            data = new File(this.directory + type.toString() + File.separator, "");
            if (!data.exists()) {
                if (data.mkdir()) {
                    debug("Created missing directory: " + this.directory + type.toString());
                }
            }
            if (isCombinedLogging()) {
                data = new File(this.directory + "combined" + File.separator, "");
                if (!data.exists()) {
                    if (data.mkdir()) {
                        debug("Created missing directory: " + this.directory + type.toString());
                    }
                }
            }
            DateFormat logFormat = new SimpleDateFormat(this.format);
            Date date = new Date();
            if (isCombinedLogging()) {
                data = new File(this.directory + "combined" + File.separator
                        + logFormat.format(date) + "-combined.log");
                if (!data.exists()) {
                    try {
                        if (!data.createNewFile()) {
                            error("Failed creating file for logging: '" + data.getName() + "'.");
                        }
                    } catch (IOException e) {
                        stackTrace(e);
                    }
                }
            }
            data = new File(this.directory + type.toString() + File.separator
                    + logFormat.format(date) + "-" + type.toString() + ".log");
            if (!data.exists()) {
                try {
                    if (!data.createNewFile()) {
                        error("Failed creating file for logging: '" + data.getName() + "'.");
                    }
                } catch (IOException e) {
                    stackTrace(e);
                }
            }
            if (isCombinedLogging()) {
                try {
                    DateFormat stringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    FileWriter writer = new FileWriter(this.directory
                            + "combined" + File.separator + logFormat.format(date) + "-"
                            + "combined" + ".log", true);
                    BufferedWriter buffer = new BufferedWriter(writer);
                    buffer.write(stringFormat.format(date) + " - " + type.toString().toUpperCase()
                            + " - " + line + System.getProperty("line.separator"));
                    buffer.close();
                    writer.close();
                } catch (IOException e) {
                    stackTrace(e);
                }
            }
            try {
                DateFormat stringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                FileWriter writer = new FileWriter(this.directory
                        + type.toString() + File.separator + logFormat.format(date) + "-"
                        + type.toString() + ".log", true);
                BufferedWriter buffer = new BufferedWriter(writer);
                buffer.write(stringFormat.format(date) + " - " + line + System.getProperty("line.separator"));
                buffer.close();
                writer.close();
            } catch (IOException e) {
                stackTrace(e);
            }
        }
    }
}
