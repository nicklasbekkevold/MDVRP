package main.java.utils;

public class Memo<T> implements Lazy<T> {

    private Lazy<T> lazy;
    private T memo = null;

    public Memo(Lazy<T> lazy) {
        this.lazy = lazy;
    }

    @Override
    public T eval() {
        if (lazy != null) {
            memo = lazy.eval();
            lazy = null;
        }
        return memo;
    }
}
