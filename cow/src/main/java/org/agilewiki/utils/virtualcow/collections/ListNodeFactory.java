package org.agilewiki.utils.virtualcow.collections;

import org.agilewiki.utils.immutable.BaseFactory;
import org.agilewiki.utils.immutable.FactoryRegistry;
import org.agilewiki.utils.immutable.ImmutableFactory;
import org.agilewiki.utils.virtualcow.DbFactoryRegistry;

import java.nio.ByteBuffer;

/**
 * Defines how a list is serialized / deserialized.
 */
public class ListNodeFactory extends BaseFactory {

    public final ListNode nilList;
    public final NilListNodeFactory nilListNodeFactory;

    public ListNodeFactory(DbFactoryRegistry registry) {
        super(registry, registry.listNodeImplId);
        nilListNodeFactory = new NilListNodeFactory(registry);
        nilList = new ListNodeImpl(registry);
    }

    @Override
    public ImmutableFactory getImmutableFactory(Object immutable) {
        if (((ListNode) immutable).isNil())
            return nilListNodeFactory;
        return this;
    }

    @Override
    public Class getImmutableClass() {
        return ListNodeImpl.class;
    }

    @Override
    public int getDurableLength(Object immutable) {
        return ((ListNode) immutable).getDurableLength();
    }

    @Override
    public void serialize(Object immutable, ByteBuffer byteBuffer) {
        ((ListNode) immutable).serialize(byteBuffer);
    }

    @Override
    public ListNode deserialize(ByteBuffer byteBuffer) {
        return new ListNodeImpl((DbFactoryRegistry) factoryRegistry, byteBuffer);
    }
}
