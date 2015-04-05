package org.agilewiki.utils.virtualcow;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.utils.immutable.BaseRegistry;
import org.agilewiki.utils.immutable.collections.MapNode;
import org.agilewiki.utils.immutable.collections.VersionedMapNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BigVersionedListTest extends TestCase {
    int k;
    public void test() throws Exception {
        new Plant();
        try {
            Path dbPath = Paths.get("vcow.db");
            Files.deleteIfExists(dbPath);
            int maxBlockSize = 1000000;
            try (Db db = new Db(new BaseRegistry(), dbPath, maxBlockSize)) {
                db.open(true);
                for (k = 0; k < 2; ++k) {
                    MapNode tMapNode = db.dbFactoryRegistry.nilMap;
                    tMapNode = tMapNode.add("k", k);
                    tMapNode = tMapNode.add("I", 10);
                    db.update(BigVersionedListTran.class, tMapNode).call();
                }
                db.close();
            }
        } finally {
            Plant.close();
        }
    }
}