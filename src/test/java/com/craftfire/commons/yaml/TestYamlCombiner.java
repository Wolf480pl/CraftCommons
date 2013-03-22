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

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

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
        Set<YamlManager> set = new HashSet<YamlManager>();
        set.add(mgr3);
        set.add(mgr1);
        set.add(mock(YamlManager.class));

        this.combiner.setYamlManagers(set);
        assertEquals(set, this.combiner.getYamlManagers());
        assertSame(mgr3, this.combiner.getDefaultManager());
        this.combiner.addYamlManager(mgr1);
        assertEquals(set, this.combiner.getYamlManagers());
        assertTrue(this.combiner.getYamlManagers().contains(mgr1));
        assertSame(mgr3, this.combiner.getDefaultManager());
        this.combiner.addYamlManager(mgr2);
        assertThat(this.combiner.getYamlManagers(), not(equalTo(set)));
        assertTrue(this.combiner.getYamlManagers().contains(mgr2));
        assertEquals(set.size() + 1, this.combiner.getYamlManagers().size());
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
        // TODO
    }
}
