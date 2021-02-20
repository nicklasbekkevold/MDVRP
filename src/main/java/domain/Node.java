package main.java.domain;

import java.util.Objects;

public abstract class Node {

    private final int id;
    private final int x;
    private final int y;

    private float transformedX;
    private float transformedY;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        transformedX = x;
        transformedY = y;
    }

    public Node(Node node) {
        this.id = node.id;
        this.x = node.x;
        this.y = node.y;
        transformedX = node.transformedX;
        transformedY = node.transformedY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y);
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

    public float distance (final Node other) { return (float) Math.hypot(x - other.getX(), y - other.getY()); }

    public float getTransformedX() { return transformedX; }

    public float getTransformedY() { return transformedY; }

    public void translate(final int horizontalChange, final int verticalChange) {
        transformedX += horizontalChange;
        transformedY += verticalChange;
    }

    public void scale(final float scalingFactor) {
        transformedX *= scalingFactor;
        transformedY *= scalingFactor;
    }

}
