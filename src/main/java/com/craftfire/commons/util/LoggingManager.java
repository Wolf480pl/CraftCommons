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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingManager {
    private final Logger logger;
    private String prefix, directory, format = "yyyy-MM-dd";
    private File dir;
    private boolean debug = false, logging = false, combined = true, stackTraces = false;
    private Map<String, PrintWriter> writers = new HashMap<String, PrintWriter>();
    protected static final String newline = System.getProperty("line.separator");
    protected static final Level debugLevel = new Level("DEBUG", Level.INFO.intValue() + 1) {
    };

    public LoggingManager(String logger, String prefix) {
        this.logger = Logger.getLogger(logger);
        this.prefix = prefix;
    }

    public static enum Type {
        INFO(Level.INFO, "info"),
        ERROR(Level.SEVERE, "errors"),
        DEBUG(debugLevel, "debug"),
        WARNING(Level.WARNING, "warnings");
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
            this.dir = null;
        } else {
            this.dir = new File(directory, "");
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
        this.logging = (this.dir != null) && logging;
    }

    public boolean isShowFullStackTraces() {
        return this.stackTraces;
    }

    public void setShowFullStackTraces(boolean fullStackTraces) {
        this.stackTraces = fullStackTraces;
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
        log(Type.INFO, toFile, toConsole, line, null);
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
        warning(advancedBox("WARNING") + ((message != null) ? (newline + message) : ""), false);
    }

    protected String advancedBox(String title) {
        if ((title.length() & 1) == 0) {
            title = title + " ";
        }
        int dashNum = (77 - title.length() - 2) / 2;
        char[] chars = new char[dashNum];
        Arrays.fill(chars, '-');
        String dashes = new String(chars);
        return newline
                + "|-----------------------------------------------------------------------------|"
                + newline
                + "|" + dashes + " " + title + " " + dashes + "|"
                + newline
                + "|-----------------------------------------------------------------------------|"
                + newline;
    }

    public void stackTrace(final Throwable e) {
        stackTrace(e, (List<String>) null);
    }

    @Deprecated
    public void stackTrace(final Throwable e, Map<Integer, String> extra) {
        stackTrace(e, new ArrayList<String>(extra.values()));
    }

    public void stackTrace(final Throwable e, List<String> extra) {
        stackTrace(Type.WARNING, e, extra);
    }

    public void stackTrace(Type type, final Throwable e, List<String> extra) {
        String box = advancedBox(type.name().toUpperCase());
        if (this.stackTraces) {
            this.logger.log(type.getLevel(), this.prefix + " " + box + formatExtra(extra), e);
        } else {
            StringBuilder builder = new StringBuilder(box);
            builder.append("Class name: " + e.getStackTrace()[1].getClassName() + newline);
            builder.append("Error message: " + e.getMessage() + newline);
            builder.append("Error cause: " + e.getCause() + newline);
            builder.append("File name: " + e.getStackTrace()[1].getFileName() + newline);
            builder.append("Function name: " + e.getStackTrace()[1].getMethodName() + newline);
            builder.append("Error line: " + e.getStackTrace()[1].getLineNumber() + newline);
            if (isLogging()) {
                DateFormat logFormat = new SimpleDateFormat(this.format);
                Date date = new Date();
                builder.append("Check log file: " + this.dir.getPath() + type.getFilename() + File.separator + logFormat.format(date) + "-" + type.getFilename() + ".log");
            } else {
                builder.append("Enable logging in the config to get more information about the error.");
            }
            log(type, false, true, builder.toString(), null);
        }

        toFile(type, "--------------------------- STACKTRACE ERROR ---------------------------");
        toFile(type, "Class name: " + e.getStackTrace()[1].getClassName());
        toFile(type, "Error message: " + e.getMessage());
        toFile(type, "Error cause: " + e.getCause());
        toFile(type, "File name: " + e.getStackTrace()[1].getFileName());
        toFile(type, "Function name: " + e.getStackTrace()[1].getMethodName());
        toFile(type, "Error line: " + e.getStackTrace()[1].getLineNumber());
        if (extra != null) {
            for (String str : extra) {
                toFile(type, str);
            }
        }
        toFile(type, "--------------------------- STACKTRACE START ---------------------------", e);
        toFile(type, "---------------------------- STACKTRACE END ----------------------------");
    }

    public void logError(String error) {
        toFile(Type.ERROR, error);
    }

    public void log(Type type, boolean toFile, boolean toConsole, String message, Throwable t) {
        if (toConsole) {
            this.logger.log(type.getLevel(), this.prefix + " " + message, t);
        }
        if (toFile) {
            toFile(type, message, t);
        }
    }

    public void closeWriters() {
        for (PrintWriter writer : this.writers.values()) {
            writer.close();
        }
    }

    protected String formatExtra(List<String> extra) {
        if (extra == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String str : extra) {
            builder.append(str + newline);
        }
        return builder.toString();
    }

    private void toFile(Type type, String line) {
        toFile(type, line, null);
    }

    private void toFile(Type type, String message, Throwable t) {
        if (!this.logging) {
            return;
        }

        if (!this.dir.exists()) {
            if (this.dir.mkdir()) {
                debug("Created missing directory: " + this.dir.getPath(), type != Type.DEBUG);
            }
        }
        DateFormat logFormat = new SimpleDateFormat(this.format);
        Date date = new Date();

        if (isCombinedLogging()) {
            toFile(type, message, t, date, this.dir, "combined", logFormat.format(date) + "-combined.log");
        }

        toFile(type, message, t, date, this.dir, type.getFilename(), logFormat.format(date) + "-" + type.getFilename() + ".log");
    }

    private void toFile(Type type, String message, Throwable throwable, Date date, File dir, String subdirectory, String fileName) {
        PrintWriter print;
        if (this.writers.containsKey(subdirectory)) {
            print = this.writers.get(subdirectory);
        } else {
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
                    log(Type.ERROR, type != Type.ERROR, true, "Failed creating file for logging: '" + file.getPath() + "'.", e);
                }
            }
            try {
                print = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            } catch (IOException e) {
                log(Type.ERROR, type != Type.ERROR, true, "Exception when writing to log file.", e);
                return;
            }
            this.writers.put(subdirectory, print);
        }
        DateFormat stringFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        print.println(stringFormat.format(date) + " - " + type.toString().toUpperCase() + " - " + message);
        if (throwable != null) {
            throwable.printStackTrace(print);
        }
        print.flush();
    }

    @Override
    protected void finalize() {
        closeWriters();
    }
}
