package org.agilewiki.utils.virtualcow.collections;

import org.agilewiki.utils.immutable.BaseFactory;
import org.agilewiki.utils.virtualcow.DbFactoryRegistry;

import java.nio.ByteBuffer;

/**
 * Defines how a nil map node is serialized / deserialized.
 */
public class NilMapNodeFactory extends BaseFactory {

    public final DbFactoryRegistry registry;

    public NilMapNodeFactory(DbFactoryRegistry registry) {
        super(registry, registry.nilMapId);
        this.registry = registry;
    }

    @Override
    public Class getImmutableClass() {
        return getClass();
    }

    @Override
    public void match(Object durable) {
        if (!((MapNode) durable).isNil())
            throw new IllegalArgumentException("The immutable object is not a nil map node");
    }

    @Override
    public int getDurableLength(Object durable) {
        return 2;
    }

    @Override
    public void serialize(Object durable, ByteBuffer byteBuffer) {
    }

    @Override
    public MapNode deserialize(ByteBuffer byteBuffer) {
        return registry.nilMap;
    }
}
