package gamemode.forest.util;

import java.util.Objects;

/**
 * An immutable value object representing a 2D integer grid coordinate.
 * <p>
 * Used as a key in {@link WorldManager}'s chunk and spawn maps to identify
 * chunks by their column ({@code x}) and row ({@code y}) index in the world grid.
 * Equality and hashing are based on coordinate values, making it safe to use
 * as a {@link java.util.Map} key or in a {@link java.util.Set}.
 * </p>
 */
public class WorldPoint {

    /** The column index of this grid coordinate. */
    private final int x;

    /** The row index of this grid coordinate. */
    private final int y;

    /**
     * Constructs a {@code WorldPoint} with the given grid coordinates.
     *
     * @param x the column index
     * @param y the row index
     */
    public WorldPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the column index of this grid coordinate.
     *
     * @return the X index
     */
    public int getX() { return x; }

    /**
     * Returns the row index of this grid coordinate.
     *
     * @return the Y index
     */
    public int getY() { return y; }

    /**
     * Returns {@code true} if the given object is a {@code WorldPoint}
     * with the same X and Y values as this one.
     *
     * @param o the object to compare
     * @return {@code true} if both coordinates are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldPoint)) return false;
        WorldPoint that = (WorldPoint) o;
        return x == that.x && y == that.y;
    }

    /**
     * Returns a hash code based on both coordinate values, consistent with
     * {@link #equals(Object)}.
     *
     * @return the hash code for this point
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}