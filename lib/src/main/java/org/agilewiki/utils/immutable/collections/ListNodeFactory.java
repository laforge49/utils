package org.agilewiki.utils.immutable.collections;

import org.agilewiki.utils.immutable.BaseFactory;
import org.agilewiki.utils.immutable.FactoryRegistry;

import java.nio.ByteBuffer;

/**
 * Defines how a list is serialized / deserialized.
 */
public class ListNodeFactory extends BaseFactory {

    public final char nilListId;
    public final ListNode nilList;

    public ListNodeFactory(FactoryRegistry factoryRegistry, char id, char nilListId) {
        super(factoryRegistry, id);
        this.nilListId = nilListId;
        new NilListNodeFactory(this, nilListId);
        nilList = new ListNode(this);
    }

    @Override
    public Class getImmutableClass() {
        return ListNode.class;
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
        return new ListNode(this, byteBuffer);
    }
}