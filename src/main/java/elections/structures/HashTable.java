package elections.structures;

public class HashTable<K, V> {

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

    private Entry<K, V>[] table;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        this.capacity = capacity;
        table = new Entry[capacity];
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(K key, V value) {
        int index = hash(key);

        while (table[index] != null && !table[index].deleted) {
            if (table[index].key.equals(key)) {
                table[index].value = value;
                return;
            }
            index = (index + 1) % capacity;
        }

        table[index] = new Entry<>(key, value);
        size++;
    }

    public V get(K key) {
        int index = hash(key);

        while (table[index] != null) {
            if (!table[index].deleted && table[index].key.equals(key)) {
                return table[index].value;
            }
            index = (index + 1) % capacity;
        }
        return null;
    }
    // get from index
    public V getFromIndex(int index) {
        if (index < 0 || index >= capacity) return null;
        if (table[index] == null || table[index].deleted) return null;
        return table[index].value;
    }


    public void remove(K key) {
        int index = hash(key);

        while (table[index] != null) {
            if (!table[index].deleted && table[index].key.equals(key)) {
                table[index].deleted = true;
                size--;
                return;
            }
            index = (index + 1) % capacity;
        }
    }
}
