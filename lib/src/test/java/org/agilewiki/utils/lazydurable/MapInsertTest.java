package org.agilewiki.utils.lazydurable;

import junit.framework.TestCase;
import org.agilewiki.utils.maplist.ListAccessor;
import org.agilewiki.utils.maplist.MapAccessor;

import java.util.Iterator;

public class MapInsertTest extends TestCase {
    public void test() throws Exception {

        LazyDurableMapNode m1 = LazyDurableMapNode.MAP_NIL.add("1", "a", 2);

        assertEquals(1, m1.totalSize("1"));

        assertEquals(1, m1.flatKeys(3).size());

        ListAccessor a1 = m1.listAccessor("1", 3);
        assertEquals("a", a1.get(0));

        assertEquals("a", m1.copyList("1").getExistingValue(0, 4));

        MapAccessor ma = m1.mapAccessor();
        assertEquals("1", ma.firstKey());
        assertEquals("1", ma.lastKey());
        assertEquals(1, ma.flatMap().size());
        assertEquals(1, ma.size());

        assertEquals("1", ma.higherKey(""));
        assertEquals("1", ma.ceilingKey(""));
        assertEquals("1", ma.ceilingKey("1"));
        assertEquals("1", ma.lowerKey("9"));
        assertEquals("1", ma.floorKey("9"));
        assertEquals("1", ma.floorKey("1"));

        Iterator<ListAccessor> it = ma.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next().size());
        assertFalse(it.hasNext());
    }
}