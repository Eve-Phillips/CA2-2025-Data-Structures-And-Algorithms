package com.example.ca22025dataalgorithmsandstructures.structures;

public class MyArray<T> {
    private T[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public MyArray() {
        data = (T[]) new Object[10];
        size = 0;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public void add(T value) {
        ensureCapacity(); // Will write own version of this
        data[size++] = value;
    }

    public void remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        for (int i = index; i < size - 1; i++)
            data[i] = data[i + 1];

        size--;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size < data.length) return;

        T[] newData = (T[]) new Object[data.length * 2];
        for (int i = 0; i < size; i++)
            newData[i] = data[i];
        data = newData;
    }
}
