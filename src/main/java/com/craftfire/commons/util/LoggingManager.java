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
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingManager {
    private final Logger logger;
    private String prefix, directory, format = "yyyy-MM-dd";
    private boolean debug = false, logging = false, combined = true, stackTraces = false;
    protected static final String newline = System.getProperty("line.separator");
    protected static final Level debugLevel = new Level("DEBUG", Level.INFO.intValue() + 1) {
    };

    public LoggingManager(String logger, String prefix) {
        this.logger = Logger.getLogger(logger);
        this.prefix = prefix;
    }

    public static enum Type {
        INFO(Level.INFO, "info"), ERROR(Level.SEVERE, "errors"), DEBUG(debugLevel, "debug"), WARNING(Level.WARNING, "warnings");
        private static Map<Level, Type> byLevel = new HashMap<Level, Type>();
        private final Level level;
        private final String name;

        static {
            for (Type t : values()) {
                byLevel.put(t.level, t);
            }
        }

        private Type(Level level, String filename) {
            this.level = level;
            this.name = filename;
        }

        public Level getLevel() {
            return this.level;
        }

        public String getFilename() {
            return this.name;
        }

        public static Type byLevel(Level level) {
            return byLevel.get(level);
        }
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

    public boolean isPrintStackTraces() {
        return this.stackTraces;
    }

    public void setPrintStackTraces(boolean printStackTraces) {
        this.stackTraces = printStackTraces;
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

    public void info(String line, boolean toFile) {
        info(line, toFile, true);
    }

    public void info(String line, boolean toFile, boolean toConsole) {
        if (toConsole) {
            this.logger.info(this.prefix + " " + line);
        }
        if (toFile) {
            toFile(Type.INFO, line);
        }
    }

    public void warning(String line) {
        warning(line, true);
    }

    public void warning(String line, boolean toFile) {
        warning(line, toFile, true);
    }

    public void warning(String line, boolean toFile, boolean toConsole) {
        if (toConsole) {
            this.logger.warning(this.prefix + " " + line);
        }
        if (toFile) {
            toFile(Type.WARNING, line);
        }
    }

    public void severe(String line) {
        severe(line, true);
    }

    public void severe(String line, boolean toFile) {
        severe(line, toFile, true);
    }

    public void severe(String line, boolean toFile, boolean toConsole) {
        if (toConsole) {
            this.logger.severe(this.prefix + " " + line);
        }
        if (toFile) {
            toFile(Type.ERROR, line);
        }
    }

    public void debug(String line) {
        debug(line, true);
    }

    public void debug(String line, boolean toFile) {
        debug(line, toFile, true);
    }

    public void debug(String line, boolean toFile, boolean toConsole) {
        if (isDebug()) {
            if (toConsole) {
                this.logger.log(debugLevel, this.prefix + " " + line);
            }
            if (toFile) {
                toFile(Type.DEBUG, line);
            }
        }
    }

    public void error(String error) {
        severe(error);
    }

    public void advancedWarning() {
        advancedWarning(null);
    }

    public void advancedWarning(String message) {
        warning(newline
                + "|-----------------------------------------------------------------------------|"
                + newline
                + "|---------------------------------- WARNING ----------------------------------|"
                + newline
                + "|-----------------------------------------------------------------------------|"
                + ((message != null) ? (newline + message) : ""), false);
    }

    public void stackTrace(final Throwable e) {
        stackTrace(e, null);
    }

    public void stackTrace(final Throwable e, Map<Integer, String> extra) {
        advancedWarning();
        if (this.stackTraces) {
            this.logger.log(Level.WARNING, "", e);
        } else {
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
        toFile(Type.ERROR, error);
    }

    public void log(Type type, boolean toFile, boolean toConsole, String message, Throwable t) {
        if (toConsole) {
            this.logger.log(type.getLevel(), message, t);
        }
        if (toFile) {

        }
    }

    private void toFile(Type type, String line) {
        toFile(type, line, null);
    }

    private void toFile(Type type, String line, Throwable t) {
        if (!this.logging) {
            return;
        }

        File dir = new File(this.directory, "");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                debug("Created missing directory: " + dir.getPath(), type != Type.DEBUG);
            }
        }
        DateFormat logFormat = new SimpleDateFormat(this.format);
        Date date = new Date();

        if (isCombinedLogging()) {
            toFile(type, line, t, date, dir, "combined", logFormat.format(date) + "-combined.log");
        }

        toFile(type, line, t, date, dir, type.getFilename(), logFormat.format(date) + "-" + type.getFilename() + ".log");
    }

    private void toFile(Type type, String line, Throwable throwable, Date date, File dir, String subdirectory, String fileName) {
        File subDir = new File(dir, subdirectory);
        if (!subDir.exists()) {
            if (subDir.mkdir()) {
                debug("Created missing directory: " + subDir.getPath(), type != Type.DEBUG);
            }
        }
        File file = new File(subDir, fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    severe("Failed creating file for logging: '" + file.getPath() + "'.", type != Type.ERROR);
                }
            } catch (IOException e) {
                stackTrace(e);  // TODO: Make sure we don't get an infinite recursion
            }
        }
        try {
            DateFormat stringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            PrintWriter print = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            print.write(stringFormat.format(date) + " - " + type.toString().toUpperCase() + " - " + line + newline);
            throwable.printStackTrace(print);
            print.close();
        } catch (IOException e) {
            stackTrace(e);  // TODO: Make sure we don't get an infinite recursion
        }

    }

}
