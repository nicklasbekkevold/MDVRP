package main.java.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class Pair<A> implements Collection<A> {

    public final A first;
    public final A second;

    public Pair(A first, A second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    public A get(int index) {
        switch (index) {
            case 0 -> {
                return first;
            }
            case 1 -> {
                return second;
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return first.equals(o) ||second.equals(o);
    }

    @Override
    public Iterator<A> iterator() {
        return new Iterator<A>() {
            A current = first;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public A next() {
                if (current == first) {
                    current = second;
                    return first;
                } else if (current == second) {
                    current = null;
                    return second;
                }
                return current;
            }
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{first, second};
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Cannot cast type <A> to type <T>");
    }

    @Override
    public boolean add(A a) {
        throw new UnsupportedOperationException("Pair is immutable and does not support add.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Pair is immutable and does not support remove.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.contains(first) && c.contains(second);
    }

    @Override
    public boolean addAll(Collection<? extends A> c) {
        throw new UnsupportedOperationException("Pair is immutable and does not support addAll.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Pair is immutable and does not support removeAll.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Pair is immutable and does not support retainAll.");
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?> pair = (Pair<?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }
}
