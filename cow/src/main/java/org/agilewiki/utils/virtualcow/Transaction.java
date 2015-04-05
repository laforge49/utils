package org.agilewiki.utils.virtualcow;

import org.agilewiki.utils.immutable.collections.MapNode;

/**
 * A transaction simply transforms a map list.
 */
public interface Transaction {
    /**
     * Transforms a map list.
     *
     * @param dbMapNode The map list to be transformed.
     * @param timestamp A unique timestamp identifying the transaction,
     *                  usable as the time in the versioned API.
     * @param tMapNode  The durable content of the transaction.
     * @return The replacement dbMapNode.
     */
    MapNode transform(MapNode dbMapNode, long timestamp, MapNode tMapNode);

    /**
     * Transaction timeout in milliseconds.
     * Set to Integer.MAX_VALUE by default.
     *
     * @return The max time in milliseconds the transaction can take.
     */
    default int timeoutMillis() {
        return Integer.MAX_VALUE;
    }
}
