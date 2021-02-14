package main.java.domain;

public abstract class Node {

    private final int id;
    private final int x;
    private final int y;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double distance (final Node other) {
        return Math.sqrt(
                Math.pow(x - other.getX(), 2) +
                Math.pow(y - other.getY(), 2)
        );
    }

}
