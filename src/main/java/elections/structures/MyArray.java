package elections.structures;

/**
 * A simple generic dynamic array implementation.
 *
 * This class provides array-backed storage with automatic resizing,
 * similar in behaviour to Java's ArrayList but implemented explicitly
 * for learning and control over internal behaviour.
 */
public class MyArray<T> {

    // Underlying array used to store elements
    private T[] data;

    // Number of elements currently stored (not the array capacity)
    private int size;

    /**
     * Constructs an empty MyArray with an initial capacity of 10.
     */
    @SuppressWarnings("unchecked")
    public MyArray() {
        data = (T[]) new Object[10];
        size = 0;
    }

    /**
     * @return the number of elements currently stored in the array
     */
    public int size() {
        return size;
    }

    /**
     * Retrieves the element at a given index.
     *
     * Bounds checking ensures invalid indices are not accessed.
     */
    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return data[index];
    }

    /**
     * Replaces the element at a given index with a new value.
     *
     * The size of the array is not changed.
     */
    public void set(int index, T value) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    /**
     * Appends a value to the end of the array.
     *
     * Capacity is checked before insertion to ensure
     * there is space available.
     */
    public void add(T value) {
        ensureCapacity();
        data[size++] = value;
    }

    /**
     * Removes the element at a specified index.
     *
     * Elements to the right of the removed index are shifted
     * left to maintain a contiguous array structure.
     */
    public void remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        for (int i = index; i < size - 1; i++)
            data[i] = data[i + 1];

        size--;
    }

    /**
     * Ensures that the underlying array has sufficient capacity.
     *
     * When the array is full, its capacity is doubled and
     * existing elements are copied into the new array.
     */
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size < data.length) return;

        T[] newData = (T[]) new Object[data.length * 2];

        for (int i = 0; i < size; i++)
            newData[i] = data[i];

        data = newData;
    }
}
