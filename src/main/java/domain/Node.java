package main.java.domain;

public abstract class Node {

    private final int id;
    private final int x;
    private final int y;

    private double transformedX;
    private double transformedY;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        transformedX = x;
        transformedY = y;
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

    public double distance (final Node other) { return Math.hypot(x - other.getX(), y - other.getY()); }

    public double getTransformedX() { return transformedX; }

    public double getTransformedY() { return transformedY; }

    public void translate(final int horizontalChange, final int verticalChange) {
        transformedX += horizontalChange;
        transformedY += verticalChange;
    }

    public void scale(final double scalingFactor) {
        transformedX *= scalingFactor;
        transformedY *= scalingFactor;
    }


}
