package main.java.domain;

import main.java.utils.Memorandum;
import main.java.utils.SymmetricPair;

import java.util.Objects;
import java.util.function.Function;

public abstract class Node {

    private final static Function<SymmetricPair<Node>, Double> euclideanDistance = nodes -> Math.hypot(nodes.first.x - nodes.second.x, nodes.first.y - nodes.second.y);
    private final static Function<SymmetricPair<Node>, Double> memoizedDistances = Memorandum.memoize(euclideanDistance);

    private final int id;
    private final int x;
    private final int y;

    private double transformedX;
    private double transformedY;
    private boolean isBorderLine = false;

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
        isBorderLine = node.isBorderLine;
    }

    public int getId() {
        return id;
    }

    public double distance (final Node other) { return memoizedDistances.apply(new SymmetricPair<>(this, other)); }

    public double getTransformedX() { return transformedX; }

    public double getTransformedY() { return transformedY; }

    public boolean isBorderLine() { return isBorderLine; }

    public void translate(final int horizontalChange, final int verticalChange) {
        transformedX += horizontalChange;
        transformedY += verticalChange;
    }

    public void scale(final double scalingFactor) {
        transformedX *= scalingFactor;
        transformedY *= scalingFactor;
    }

    public void setBorderLine() { isBorderLine = true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d=(%d, %d)", id, x, y);
    }

}
