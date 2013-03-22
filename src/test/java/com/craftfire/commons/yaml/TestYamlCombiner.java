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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;

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
    public void testGetDefaultSettings() {
        Settings settings = this.combiner.getDefaultSettings();
        Settings defaults = new Settings();
        assertEquals(defaults.getConstructor().getClass(), settings.getConstructor().getClass());
        assertEquals(defaults.getRepresenter().getClass(), settings.getRepresenter().getClass());
        compareDumperOptions(defaults.getDumperOptions(), settings.getDumperOptions());
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
