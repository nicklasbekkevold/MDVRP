package main.java.utils;

public class SymmetricPair<A> extends Pair<A, A> {

    public SymmetricPair(A first, A second) {
        super(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) && second.equals(pair.second) || first.equals(pair.second) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
}
