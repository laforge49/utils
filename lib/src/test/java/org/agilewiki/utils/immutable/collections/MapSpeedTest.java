package org.agilewiki.utils.immutable.collections;

import junit.framework.TestCase;
import org.agilewiki.utils.immutable.FactoryRegistry;
import org.agilewiki.utils.immutable.Registry;

import java.nio.ByteBuffer;

public class MapSpeedTest extends TestCase {
    public void test() throws Exception {
        Registry registry = new Registry();
        MapNode m1 = registry.nilMap;
        int c = 10;
        long t0 = System.currentTimeMillis();
        for(int i = 0; i < c; ++i) {
            m1 = m1.add("k" + i, "v" + i);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Created "+c+" entries in "+(t1 - t0)+" milliseconds");

        ByteBuffer byteBufferx = ByteBuffer.allocate(m1.getDurableLength());
        m1.writeDurable(byteBufferx);

        long t2 = System.currentTimeMillis();
        ByteBuffer byteBuffer = ByteBuffer.allocate(m1.getDurableLength());
        m1.writeDurable(byteBuffer);
        long t3 = System.currentTimeMillis();
        System.out.println("Serialization time = "+(t3 - t2)+" milliseconds");
        System.out.println("durable length = " + m1.getDurableLength());
        byteBuffer.flip();
        long t4 = System.currentTimeMillis();
        MapNode m2 = (MapNode) registry.readId(byteBuffer).deserialize(byteBuffer);
        String fk = (String) m2.firstKey();
        m2 = m2.set("k0", "upd");
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(m2.getDurableLength());
        m2.writeDurable(byteBuffer1);
        long t5 = System.currentTimeMillis();
        System.out.println("Deserialize/reserialize time = "+(t5 - t4)+" milliseconds");
        System.out.println("durable length = " + m2.getDurableLength());
    }
}