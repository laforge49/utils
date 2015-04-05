package org.agilewiki.utils.immutable.collections;

import org.agilewiki.utils.immutable.ImmutableFactory;
import org.agilewiki.utils.virtualcow.Db;
import org.agilewiki.utils.virtualcow.DbFactoryRegistry;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * An immutable map of versioned lists.
 */
public interface VersionedMapNode extends Releasable {

    /**
     * Returns the database factory registry.
     *
     * @return The registry.
     */
    DbFactoryRegistry getRegistry();

    /**
     * Returns the database.
     *
     * @return The database.
     */
    default Db getDb() {
        return getRegistry().db;
    }

    /**
     * Returns the current timestamp, a unique
     * identifier for the current transaction.
     *
     * @return The current transaction's timestamp
     */
    default long getTimestamp() {
        return getDb().getTimestamp();
    }

    VersionedMapNodeData getData();

    default boolean isNil() {
        return this == getRegistry().versionedNilMap;
    }

    default VersionedListNode getList(Comparable key) {
        if (isNil())
            return getRegistry().versionedNilList;
        return getData().getList(key);
    }

    /**
     * Returns the count of all the values in the list,
     * including deleted values.
     *
     * @param key The list identifier.
     * @return The count of all the values in the list.
     */
    default int totalSize(Comparable key) {
        return getList(key).totalSize();
    }

    /**
     * Returns a list accessor for the latest time.
     *
     * @param key The key for the list.
     * @return A list accessor for the latest time.
     */
    default ListAccessor listAccessor(Comparable key) {
        return getList(key).listAccessor(key);
    }

    /**
     * Returns a list accessor for the given time.
     *
     * @param key  The key for the list.
     * @param time The time of the query.
     * @return A list accessor for the given time.
     */
    default ListAccessor listAccessor(Comparable key, long time) {
        return getList(key).listAccessor(key, time);
    }

    /**
     * Add a non-null value to the end of the list.
     *
     * @param key   The key of the list.
     * @param value The value to be added.
     * @return The revised root node.
     */
    default VersionedMapNode add(Comparable key, Object value) {
        return add(key, -1, value);
    }

    /**
     * Add a non-null value to the list.
     *
     * @param key   The key of the list.
     * @param ndx   Where to add the value.
     * @param value The value to be added.
     * @return The revised root node.
     */
    default VersionedMapNode add(Comparable key, int ndx, Object value) {
        return add(key, ndx, value, getTimestamp(), Long.MAX_VALUE);
    }

    default VersionedMapNode add(Comparable key, int ndx, Object value, long created, long deleted) {
        if (key == null)
            throw new IllegalArgumentException("key may not be null");
        if (isNil()) {
            DbFactoryRegistry registry = getRegistry();
            VersionedListNode listNode = registry.versionedNilList.add(ndx, value, created, deleted);
            return getData().replace(1, listNode, key);
        }
        return getData().add(key, ndx, value, created, deleted);
    }

    /**
     * Mark a value as deleted.
     *
     * @param key  The key of the list.
     * @param ndx  The index of the value.
     * @return The revised node.
     */
    default VersionedMapNode remove(Comparable key, int ndx) {
        if (isNil())
            return this;
        return getData().remove(key, ndx);
    }

    /**
     * Empty the list by marking all the existing values as deleted.
     *
     * @param key  The key of the list.
     * @return The revised node.
     */
    default VersionedMapNode clearList(Comparable key) {
        if (isNil())
            return this;
        return getData().clearList(key);
    }

    /**
     * Replace the list entries with a single value.
     *
     * @param key   The key of the list.
     * @param value The new value.
     * @return The revised node.
     */
    default VersionedMapNode set(Comparable key, Object value) {
        if (value == null)
            throw new IllegalArgumentException("value may not be null");
        if (isNil()) {
            DbFactoryRegistry registry = getRegistry();
            VersionedListNode listNode = registry.versionedNilList.add(value);
            return getData().replace(1, listNode, key);
        }
        return getData().set(key, value);
    }

    /**
     * Empty the map by marking all the existing values as deleted.
     *
     * @return The currently empty versioned map.
     */
    default VersionedMapNode clearMap() {
        if (isNil())
            return this;
        return getData().clearMap();
    }

    /**
     * Perform a complete list copy.
     *
     * @return A complete, but shallow copy of the list.
     */
    default VersionedListNode copyList(Comparable key) {
        return getList(key).copyList();
    }

    /**
     * Copy everything in the list except what was deleted before a given time.
     * (This is a shallow copy, as the values in the list are not copied.)
     *
     * @param time The given time.
     * @return A shortened copy of the list without some historical values.
     */
    default VersionedListNode copyList(Comparable key, long time) {
        return getList(key).copyList(time);
    }

    /**
     * Returns a set of all keys with non-empty lists for the given time.
     *
     * @param time The time of the query.
     * @return A set of the keys with content at the time of the query.
     */
    default NavigableSet<Comparable> flatKeys(long time) {
        NavigableSet keys = new TreeSet<>();
        getData().flatKeys(keys, time);
        return keys;
    }

    /**
     * Returns a map of all the keys and values present at the given time.
     *
     * @param time The time of the query.
     * @return A map of lists.
     */
    default NavigableMap<Comparable, List> flatMap(long time) {
        NavigableMap<Comparable, List> map = new TreeMap<Comparable, List>();
        getData().flatMap(map, time);
        return map;
    }

    /**
     * Perform a complete copy.
     *
     * @return A complete, but shallow copy of the list.
     */
    default VersionedMapNode copyMap() {
        return copyMap(0L);
    }

    /**
     * Copy everything except what was deleted before a given time.
     * (This is a shallow copy, as the values in the lists are not copied.)
     *
     * @param time The given time.
     * @return A shortened copy of the map without some historical values.
     */
    default VersionedMapNode copyMap(long time) {
        return getData().copyMap(getRegistry().versionedNilMap, time);
    }

    /**
     * Returns the count of all the keys in the map, empty or not.
     *
     * @return The count of all the keys in the map.
     */
    default int totalSize() {
        if (isNil())
            return 0;
        return getData().totalSize();
    }

    /**
     * Returns the count of all the keys with a non-empty list.
     *
     * @param time The time of the query.
     * @return The current size of the map.
     */
    default int size(long time) {
        if (isNil())
            return 0;
        return getData().size(time);
    }

    /**
     * Returns the smallest key of the non-empty lists for the given time.
     *
     * @param time The time of the query.
     * @return The smallest key, or null.
     */
    default Comparable firstKey(long time) {
        if (isNil())
            return null;
        return getData().firstKey(time);
    }

    /**
     * Returns the largest key of the non-empty lists for the given time.
     *
     * @param time The time of the query.
     * @return The largest key, or null.
     */
    default Comparable lastKey(long time) {
        if (isNil())
            return null;
        return getData().lastKey(time);
    }

    /**
     * Returns the next greater key.
     *
     * @param key  The given key.
     * @param time The time of the query.
     * @return The next greater key with content at the time of the query, or null.
     */
    default Comparable higherKey(Comparable key, long time) {
        if (isNil())
            return null;
        return getData().higherKey(key, time);
    }

    /**
     * Returns the key with content that is greater than or equal to the given key.
     *
     * @param key  The given key.
     * @param time The time of the query.
     * @return The key greater than or equal to the given key, or null.
     */
    default Comparable ceilingKey(Comparable key, long time) {
        if (isNil())
            return null;
        return getData().ceilingKey(key, time);
    }

    /**
     * Returns the next smaller key.
     *
     * @param key  The given key.
     * @param time The time of the query.
     * @return The next smaller key with content at the time of the query, or null.
     */
    default Comparable lowerKey(Comparable key, long time) {
        if (isNil())
            return null;
        return getData().lowerKey(key, time);
    }

    /**
     * Returns the key with content that is smaller than or equal to the given key.
     *
     * @param key  The given key.
     * @param time The time of the query.
     * @return The key smaller than or equal to the given key, or null.
     */
    default Comparable floorKey(Comparable key, long time) {
        if (isNil())
            return null;
        return getData().floorKey(key, time);
    }

    /**
     * Returns an iterator over the non-empty list accessors.
     *
     * @param time The time of the query.
     * @return The iterator.
     */
    default Iterator<ListAccessor> iterator(long time) {
        return new Iterator<ListAccessor>() {
            Comparable last = null;

            @Override
            public boolean hasNext() {
                if (last == null)
                    return firstKey(time) != null;
                return higherKey(last, time) != null;
            }

            @Override
            public ListAccessor next() {
                Comparable next = last == null ? firstKey(time) : higherKey(last, time);
                if (next == null)
                    throw new NoSuchElementException();
                last = next;
                return listAccessor(last, time);
            }
        };
    }

    /**
     * Returns a map accessor for the time of the current transaction.
     *
     * @return A map accessor for the latest time.
     */
    default MapAccessor mapAccessor() {
        return mapAccessor(getTimestamp());
    }

    /**
     * Returns a map accessor for a given time.
     *
     * @param time The time of the query.
     * @return A map accessor for the given time.
     */
    default MapAccessor mapAccessor(long time) {
        return new MapAccessor() {

            @Override
            public long time() {
                return time;
            }

            @Override
            public int size() {
                return VersionedMapNode.this.size(time);
            }

            @Override
            public ListAccessor listAccessor(Comparable key) {
                return VersionedMapNode.this.listAccessor(key, time);
            }

            @Override
            public NavigableSet<Comparable> flatKeys() {
                return VersionedMapNode.this.flatKeys(time);
            }

            @Override
            public Comparable firstKey() {
                return VersionedMapNode.this.firstKey(time);
            }

            @Override
            public Comparable lastKey() {
                return VersionedMapNode.this.lastKey(time);
            }

            @Override
            public Comparable higherKey(Comparable key) {
                return VersionedMapNode.this.higherKey(key, time);
            }

            @Override
            public Comparable ceilingKey(Comparable key) {
                return VersionedMapNode.this.ceilingKey(key, time);
            }

            @Override
            public Comparable lowerKey(Comparable key) {
                return VersionedMapNode.this.lowerKey(key, time);
            }

            @Override
            public Comparable floorKey(Comparable key) {
                return VersionedMapNode.this.floorKey(key, time);
            }

            @Override
            public Iterator<ListAccessor> iterator() {
                return VersionedMapNode.this.iterator(time);
            }

            @Override
            public NavigableMap<Comparable, List> flatMap() {
                return VersionedMapNode.this.flatMap(time);
            }
        };
    }

    /**
     * Returns the size of a byte array needed to serialize this object,
     * including the space needed for the durable id.
     *
     * @return The size in bytes of the serialized data.
     */
    int getDurableLength();

    /**
     * Write the durable to a byte buffer.
     *
     * @param byteBuffer The byte buffer.
     */
    void writeDurable(ByteBuffer byteBuffer);

    /**
     * Serialize this object into a ByteBuffer.
     *
     * @param byteBuffer Where the serialized data is to be placed.
     */
    void serialize(ByteBuffer byteBuffer);

    @Override
    default void releaseAll() {
        getData().releaseAll();
    }

    @Override
    default Object resize(int maxSize, int maxBlockSize) {
        return getData().resize(maxSize, maxBlockSize);
    }

    /**
     * Returns a ByteBuffer loaded with the serialized contents of the immutable.
     *
     * @return The loaded ByteBuffer.
     */
    default ByteBuffer toByteBuffer() {
        ImmutableFactory factory = getRegistry().getImmutableFactory(this);
        return factory.toByteBuffer(this);
    }
}