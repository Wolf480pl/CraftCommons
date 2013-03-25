package com.craftfire.commons.yaml;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;

import com.craftfire.commons.util.LoggingManager;

public class TestYamlCombiner {
    private YamlCombiner combiner;

    @Before
    public void setup() {
        this.combiner = new YamlCombiner();
    }

    @Test
    public void testGetSetYamlManagers() {
        assertTrue(this.combiner.getYamlManagers().isEmpty());
        assertNull(this.combiner.getDefaultManager());

        Set<YamlManager> set = new HashSet<YamlManager>();
        set.add(mock(YamlManager.class));
        set.add(mock(YamlManager.class));
        set.add(mock(YamlManager.class));

        this.combiner.setYamlManagers(set);
        assertEquals(set, this.combiner.getYamlManagers());
        assertTrue(set.contains(this.combiner.getDefaultManager()));

        this.combiner.setYamlManagers(null);
        assertTrue(this.combiner.getYamlManagers().isEmpty());
        assertNull(this.combiner.getDefaultManager());
    }

    @Test
    public void testAddYamlManager() {
        assertTrue(this.combiner.getYamlManagers().isEmpty());
        assertNull(this.combiner.getDefaultManager());

        YamlManager mgr1 = mock(YamlManager.class);
        this.combiner.addYamlManager(mgr1);
        assertTrue(this.combiner.getYamlManagers().contains(mgr1));
        assertEquals(1, this.combiner.getYamlManagers().size());
        assertSame(mgr1, this.combiner.getDefaultManager());

        YamlManager mgr2 = mock(YamlManager.class);
        YamlManager mgr3 = mock(YamlManager.class);
        List<YamlManager> list = new ArrayList<YamlManager>();
        list.add(mgr3);
        list.add(mgr1);
        list.add(mock(YamlManager.class));

        this.combiner.setYamlManagers(list);
        assertEquals(new HashSet<YamlManager>(list), this.combiner.getYamlManagers());
        assertSame(mgr3, this.combiner.getDefaultManager());
        this.combiner.addYamlManager(mgr1);
        assertEquals(new HashSet<YamlManager>(list), this.combiner.getYamlManagers());
        assertTrue(this.combiner.getYamlManagers().contains(mgr1));
        assertSame(mgr3, this.combiner.getDefaultManager());
        this.combiner.addYamlManager(mgr2);
        assertThat(this.combiner.getYamlManagers(), not(equalTo((Set<YamlManager>) new HashSet<YamlManager>(list))));
        assertTrue(this.combiner.getYamlManagers().contains(mgr2));
        assertEquals(list.size() + 1, this.combiner.getYamlManagers().size());
        assertSame(mgr3, this.combiner.getDefaultManager());

        try {
            this.combiner.addYamlManager(null);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        assertSame(mgr3, this.combiner.getDefaultManager());
        assertFalse(this.combiner.getYamlManagers().isEmpty());
    }

    @Test
    public void testSetDefaultManager() {
        YamlManager mgr1 = mock(YamlManager.class);
        YamlManager mgr2 = mock(YamlManager.class);

        try {
            this.combiner.setDefaultManager(mgr1);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        assertNull(this.combiner.getDefaultManager());

        List<YamlManager> list = new ArrayList<YamlManager>();
        list.add(mock(YamlManager.class));
        list.add(mgr1);
        list.add(mock(YamlManager.class));

        this.combiner.setYamlManagers(list);
        this.combiner.setDefaultManager(mgr1);
        assertSame(mgr1, this.combiner.getDefaultManager());

        try {
            this.combiner.setDefaultManager(mgr2);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        assertSame(mgr1, this.combiner.getDefaultManager());
    }

    @Test
    public void testGetSetDefaultSettings() {
        Settings settings = this.combiner.getDefaultSettings();
        Settings defaults = new Settings();
        // Make sure they are identical
        assertEquals(defaults.getConstructor().getClass(), settings.getConstructor().getClass());
        assertEquals(defaults.getRepresenter().getClass(), settings.getRepresenter().getClass());
        compareDumperOptions(defaults.getDumperOptions(), settings.getDumperOptions());
        assertEquals(defaults.getResolver().getClass(), settings.getResolver().getClass());
        assertEquals(defaults.getLogger().getPrefix(), settings.getLogger().getPrefix());
        assertSame(defaults.getLogger().getLogger(), settings.getLogger().getLogger());
        assertEquals(defaults.getSeparator(), settings.getSeparator());
        assertEquals(defaults.isCaseSensitive(), settings.isCaseSensitive());
        assertEquals(defaults.isMultiDocument(), settings.isMultiDocument());

        settings = mock(Settings.class);
        this.combiner.setDefaultSettings(settings);
        assertSame(settings, this.combiner.getDefaultSettings());
    }

    @Test
    public void testGetFiles() {
        Set<File> set1 = new HashSet<File>();
        Set<File> set2 = new HashSet<File>();
        Set<File> set3 = new HashSet<File>();

        File file1 = mock(File.class);
        set1.add(mock(File.class));
        set1.add(file1);
        set1.add(mock(File.class));
        set2.add(mock(File.class));
        set3.add(file1);
        set3.add(mock(File.class));

        List<YamlManager> mgrs = new ArrayList<YamlManager>();
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        stub(mgrs.get(0).getFiles()).toReturn(set1);
        stub(mgrs.get(1).getFiles()).toReturn(set2);
        stub(mgrs.get(2).getFiles()).toReturn(set3);

        this.combiner.setYamlManagers(mgrs);
        Set<File> files = this.combiner.getFiles();
        assertTrue(files.containsAll(set1));
        assertTrue(files.containsAll(set2));
        assertTrue(files.containsAll(set3));

        verify(mgrs.get(0)).getFiles();
        verify(mgrs.get(1)).getFiles();
        verify(mgrs.get(2)).getFiles();
    }

    @Test
    public void testGetLogger() {
        Settings settings = mock(Settings.class);
        LoggingManager mgr = mock(LoggingManager.class);
        stub(settings.getLogger()).toReturn(mgr);

        this.combiner.setDefaultSettings(settings);
        assertSame(mgr, this.combiner.getLogger());

        verify(settings).getLogger();
    }

    @Test
    public void testSetLoggingManager() {
        List<YamlManager> mgrs = new ArrayList<YamlManager>();
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        this.combiner.setYamlManagers(mgrs);

        LoggingManager logger = mock(LoggingManager.class);
        this.combiner.setLoggingManager(logger);
        assertSame(logger, this.combiner.getDefaultSettings().getLogger());

        verify(mgrs.get(0)).setLoggingManager(logger);
        verify(mgrs.get(1)).setLoggingManager(logger);
        verify(mgrs.get(2)).setLoggingManager(logger);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRootNode() {
        List<YamlManager> mgrs = new ArrayList<YamlManager>();
        YamlManager mgr = mock(YamlManager.class);
        mgrs.add(mgr);
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        this.combiner.setYamlManagers(mgrs);
        this.combiner.setDefaultManager(mgr);

        YamlNode node = mock(YamlNode.class);
        stub(mgr.getRootNode()).toReturn(node);

        assertSame(node, this.combiner.getRootNode());
        verify(mgr).getRootNode();

        YamlNode node1 = mock(YamlNode.class);
        this.combiner.setRootNode(node1);
        assertSame(mgr, this.combiner.getDefaultManager());
        assertEquals(1, this.combiner.getYamlManagers().size());
        assertTrue(this.combiner.getYamlManagers().contains(mgr));
        verify(mgr).setRootNode(node1);
    }

    @Test
    public void testExist() {
        List<YamlManager> mgrs = new ArrayList<YamlManager>();
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        mgrs.add(mock(YamlManager.class));
        this.combiner.setYamlManagers(mgrs);

        stub(mgrs.get(0).exist("atlas.is.a.detector")).toReturn(false);
        stub(mgrs.get(1).exist("atlas.is.a.detector")).toReturn(true);
        stub(mgrs.get(2).exist("atlas.is.a.detector")).toReturn(false);

        assertTrue(this.combiner.exist("atlas.is.a.detector"));
        verify(mgrs.get(1)).exist("atlas.is.a.detector");

        stub(mgrs.get(0).exist("cms.is.a.detector.too")).toReturn(false);
        stub(mgrs.get(1).exist("cms.is.a.detector.too")).toReturn(false);
        stub(mgrs.get(2).exist("cms.is.a.detector.too")).toReturn(false);

        assertFalse(this.combiner.exist("cms.is.a.detector.too"));
        verify(mgrs.get(0)).exist("cms.is.a.detector.too");
        verify(mgrs.get(1)).exist("cms.is.a.detector.too");
        verify(mgrs.get(2)).exist("cms.is.a.detector.too");
    }

    private void compareDumperOptions(DumperOptions a, DumperOptions b) {
        assertEquals(a.getDefaultFlowStyle(), b.getDefaultFlowStyle());
        assertEquals(a.getDefaultScalarStyle(), b.getDefaultScalarStyle());
        assertEquals(a.getIndent(), b.getIndent());
        assertEquals(a.getLineBreak(), b.getLineBreak());
        assertEquals(a.getTags(), b.getTags());
        assertEquals(a.getTimeZone(), b.getTimeZone());
        assertEquals(a.getVersion(), b.getVersion());
        assertEquals(a.getWidth(), b.getWidth());
        assertEquals(a.isAllowReadOnlyProperties(), b.isAllowReadOnlyProperties());
        assertEquals(a.isAllowUnicode(), b.isAllowUnicode());
        assertEquals(a.isCanonical(), b.isCanonical());
        assertEquals(a.isExplicitEnd(), b.isExplicitEnd());
        assertEquals(a.isExplicitStart(), b.isExplicitStart());
        assertEquals(a.isPrettyFlow(), b.isPrettyFlow());
    }
}
