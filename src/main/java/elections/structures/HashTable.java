package elections.structures;

/**
 * A simple generic hash table implementation using open addressing
 * with linear probing for collision resolution.
 *
 * This implementation supports basic put, get, and remove operations
 * and is designed for educational use in a data structures context.
 */
public class HashTable<K, V> {

    /**
     * Represents a single key–value pair stored in the hash table.
     *
     * The deleted flag is used to support removals without breaking
     * probe chains created by linear probing.
     */
    private class Entry<K, V> {
        K key;
        V value;
        boolean deleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }
    }

    // Underlying array used to store entries
    private Entry<K, V>[] table;

    // Maximum number of slots in the table
    private int capacity;

    // Number of active (non-deleted) entries currently stored
    private int size;

    /**
     * Constructs a hash table with a fixed capacity.
     *
     * @param capacity number of slots available in the table
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        this.capacity = capacity;
        table = new Entry[capacity];
        size = 0;
    }

    /**
     * Computes the array index for a given key.
     *
     * The hash code is normalised to ensure a non-negative index
     * within the bounds of the table capacity.
     */
    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    /**
     * Inserts a key–value pair into the hash table.
     *
     * If the key already exists, its value is updated.
     * Collisions are resolved using linear probing.
     */
    public void put(K key, V value) {
        int index = hash(key);

        // Probe forward until an empty or reusable slot is found
        while (table[index] != null && !table[index].deleted) {

            // Update value if the key already exists
            if (table[index].key.equals(key)) {
                table[index].value = value;
                return;
            }

            index = (index + 1) % capacity;
        }

        table[index] = new Entry<>(key, value);
        size++;
    }

    /**
     * Retrieves a value associated with the given key.
     *
     * Linear probing is used to follow the same probe sequence
     * as insertion.
     *
     * @return the value if found, otherwise null
     */
    public V get(K key) {
        int index = hash(key);

        while (table[index] != null) {
            if (!table[index].deleted &&
                    table[index].key.equals(key)) {
                return table[index].value;
            }
            index = (index + 1) % capacity;
        }
        return null;
    }

    /**
     * Retrieves a value directly from a specific table index.
     *
     * This is primarily used to allow iteration over the table
     * contents from outside the class.
     *
     * @return the value at the given index, or null if invalid or deleted
     */
    public V getFromIndex(int index) {
        if (index < 0 || index >= capacity) return null;
        if (table[index] == null || table[index].deleted) return null;
        return table[index].value;
    }

    /**
     * Removes a key–value pair from the hash table.
     *
     * Entries are marked as deleted rather than physically removed
     * to preserve probe chains for future searches.
     */
    public void remove(K key) {
        int index = hash(key);

        while (table[index] != null) {
            if (!table[index].deleted &&
                    table[index].key.equals(key)) {

                table[index].deleted = true;
                size--;
                return;
            }
            index = (index + 1) % capacity;
        }
    }
}
