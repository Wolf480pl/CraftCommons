package com.craftfire.commons.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class TestSimpleYamlManager {
    private static Random rnd = new Random();
    private SimpleYamlManager manager;

    @Before
    public void setup() throws IOException {
        this.manager = new SimpleYamlManager(new StringReader(""));
    }

    @Test
    public void testRootRedirects() throws YamlException, NoSuchFieldException, IllegalAccessException {
        boolean randomBool = rnd.nextBoolean();
        int randomInt = rnd.nextInt();
        long randomLong = rnd.nextLong();
        byte[] randomBytes = new byte[8];
        rnd.nextBytes(randomBytes);
        String randomString = new String(randomBytes);

        YamlNode mock0 = mock(YamlNode.class);
        YamlNode mock1 = mock(YamlNode.class);
        Field f = this.manager.getClass().getDeclaredField("root");
        f.setAccessible(true);
        f.set(this.manager, mock0);

        stub(mock0.getFinalNodeCount()).toReturn(randomInt);
        assertEquals(randomInt, this.manager.getFinalNodeCount());
        verify(mock0).getFinalNodeCount();

        stub(mock0.hasNode("bob.has.a.dog")).toReturn(randomBool).toReturn(true);
        assertEquals(randomBool, this.manager.exist("bob.has.a.dog"));
        verify(mock0).hasNode("bob.has.a.dog");

        stub(mock0.getNode("bob.has.a.dog")).toReturn(mock1);
        assertSame(mock1, this.manager.getNode("bob.has.a.dog"));
        verify(mock0).getNode("bob.has.a.dog");

        stub(mock1.getBool(true)).toReturn(!randomBool);
        assertEquals(!randomBool, this.manager.getBoolean("bob.has.a.dog", true));
        verify(mock1).getBool(true);

        stub(mock1.getString("blah")).toReturn(randomString);
        assertEquals(randomString, this.manager.getString("bob.has.a.dog", "blah"));
        verify(mock1).getString("blah");

        stub(mock1.getInt((int) randomLong)).toReturn(randomInt);
        assertEquals(randomInt, this.manager.getInt("bob.has.a.dog", (int) randomLong));
        verify(mock1).getInt((int) randomLong);

        stub(mock1.getLong(randomInt)).toReturn(randomLong);
        assertEquals(randomLong, this.manager.getLong("bob.has.a.dog", randomInt));
        verify(mock1).getLong(randomInt);

        stub(mock0.getNode("bob.has.a.dog", true)).toReturn(mock1);
        this.manager.setNode("bob.has.a.dog", "test" + randomInt);
        verify(mock0).getNode("bob.has.a.dog", true);
        verify(mock1).setValue("test" + randomInt);

        verify(mock0, times(5)).getNode("bob.has.a.dog");
        verify(mock0, times(5)).hasNode("bob.has.a.dog");

        YamlManager mgrMock = mock(YamlManager.class);
        stub(mgrMock.getRootNode()).toReturn(mock1);
        @SuppressWarnings("unchecked")
        List<YamlNode> list = mock(List.class);
        stub(mock1.getChildrenList()).toReturn(list);

        this.manager.addNodes(mgrMock);
        verify(mock0).addChildren(list);
        verify(mgrMock, atLeastOnce()).getRootNode();
        verify(mock1).getChildrenList();

        @SuppressWarnings("unchecked")
        Map<String, Object> map = mock(Map.class);
        this.manager.addNodes(map);
        verify(mock0).addChildren(map);
    }

    @Test
    public void testDefaults() {
        boolean randomBool = rnd.nextBoolean();
        int randomInt = rnd.nextInt();
        long randomLong = rnd.nextLong();
        byte[] randomBytes = new byte[8];
        rnd.nextBytes(randomBytes);
        String randomString = new String(randomBytes);

        SimpleYamlManager spy = spy(this.manager);

        doReturn(randomBool).when(spy).getBoolean("bob.has.no.cat", false);
        assertEquals(randomBool, spy.getBoolean("bob.has.no.cat"));
        verify(spy).getBoolean("bob.has.no.cat", false);

        doReturn(randomString).when(spy).getString("bob.has.no.cat", null);
        assertEquals(randomString, spy.getString("bob.has.no.cat"));
        verify(spy).getString("bob.has.no.cat", null);

        doReturn(randomInt).when(spy).getInt("bob.has.no.cat", 0);
        assertEquals(randomInt, spy.getInt("bob.has.no.cat"));
        verify(spy).getInt("bob.has.no.cat", 0);

        doReturn(randomLong).when(spy).getLong("bob.has.no.cat", 0);
        assertEquals(randomLong, spy.getLong("bob.has.no.cat"));
        verify(spy).getLong("bob.has.no.cat", 0);
    }

}
