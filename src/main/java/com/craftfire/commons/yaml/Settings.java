package com.craftfire.commons.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import com.craftfire.commons.util.LoggingManager;

/**
 * A class holding settings needed by YamlManager
 */
public class Settings {
    private boolean caseSensitive = false;
    private boolean multiDocument = false;
    private String separator = ".";
    private BaseConstructor constructor;
    private Representer representer;
    private DumperOptions options;
    private Resolver resolver;
    private LoggingManager logger;

    /**
     * Creates a new Settings with default values.
     */
    public Settings() {
        this.constructor = new Constructor();
        this.representer = new EmptyNullRepresenter();
        this.resolver = new Resolver();
        this.options = new DumperOptions();
        this.options.setDefaultFlowStyle(FlowStyle.BLOCK);
        this.options.setIndent(4);
        this.logger = new LoggingManager("CraftFire.YamlManager", "[YamlManager]");
    }

    /**
     * Creates a snakeyaml parser based on these settings.
     * 
     * @return snakeyaml parser
     */
    public Yaml createYaml() {
        return new Yaml(this.constructor, this.representer, this.options, this.resolver);
    }

    /**
     * Returns the state of case-sensitive option (off by default).
     * <p>
     * If this option is on, node names will be case-sensitive. 
     * 
     * @return true if on, otherwise false
     */
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    /**
     * Sets the state of case-sensitive option (off by default).
     * <p>
     * If this option is on, node names will be case-sensitive.
     * 
     * @param caseSensitive  true to turn on, false to turn off
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * NOT IMPLEMENTED YET!
     * Checks the state of multi-document option (off by default).
     * <p>
     * This option allows to read multiple yaml documents from one file (separated by {@code ---}).
     * 
     * @return true if on, false otherwise
     */
    public boolean isMultiDocument() {
        return this.multiDocument;
    }

    /**
     * NOT IMPLEMENTED YET!
     * Sets the state of multi-document option (off by default).
     * <p>
     * This option allows to read multiple yaml documents from one file (separated by {@code ---}).
     * 
     * @param multiDocument  true to turn on, false to turn off
     */
    public void setMultiDocument(boolean multiDocument) {
        this.multiDocument = multiDocument;
    }

    /**
     * Returns the path separator to be used (default {@code "."}).
     * 
     * @return the path separator
     */
    public String getSeparator() {
        return this.separator;
    }

    /**
     * Sets the path separator to be used (default {@code "."}).
     * 
     * @param separator  the path separator
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Returns the yaml constructor to be used ({@link Constructor} by default).
     * 
     * @return the yaml constructor
     */
    public BaseConstructor getConstructor() {
        return this.constructor;
    }

    /**
     * Sets the yaml constructor to be used ({@link Constructor} by default).
     * 
     * @param constructor  the yaml constructor
     */
    public void setConstructor(BaseConstructor constructor) {
        this.constructor = constructor;
    }

    /**
     * Returns the yaml representer to be used ({@link EmptyNullRepresenter} by default).
     * 
     * @return the yaml representer
     */
    public Representer getRepresenter() {
        return this.representer;
    }

    /**
     * Sets the yaml representer to be used ({@link EmptyNullRepresenter} by default).
     * 
     * @param representer  the yaml representer
     */
    public void setRepresenter(Representer representer) {
        this.representer = representer;
    }

    /**
     * Returns the yaml dumper options.
     * <p>
     * By default it's block style, 4 spaces ident.
     * 
     * @return yaml dumper options
     */
    public DumperOptions getDumperOptions() {
        return this.options;
    }

    /**
     * Sets the yaml dumper options.
     * <p>
     * By default it's block style, 4 spaces ident.
     * 
     * @param options  yaml dumper options
     */
    public void setDumperOptions(DumperOptions options) {
        this.options = options;
    }

    /**
     * Returns the yaml resolver to be used ({@link Resolver} by default).
     * 
     * @return the yaml resolver
     */
    public Resolver getResolver() {
        return this.resolver;
    }

    /**
     * Sets the yaml resolver to be used ({@link Resolver} by default).
     * 
     * @param resolver  the yaml resolver
     */
    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Returns the logging manager to be used.
     * <p>
     * By default it's {@code new LoggingManager("CraftFire.YamlManager", "[YamlManager]")}.
     * 
     * @return the logging manager
     */
    public LoggingManager getLogger() {
        return this.logger;
    }

    /**
     * Sets the logging manager to be used.
     * <p>
     * By default it's {@code new LoggingManager("CraftFire.YamlManager", "[YamlManager]")}.
     * 
     * @param  logger                   the logging manager
     * @throws IllegalArgumentException if the logger is null
     */
    public void setLogger(LoggingManager logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Parameter 'logger' cannot be null.");
        }
        this.logger = logger;
    }
}