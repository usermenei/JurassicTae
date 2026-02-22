package gamemode.CretaceousExploration;

import java.util.Objects;

public class WorldPoint {

    private final int x;
    private final int y;

    public WorldPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldPoint)) return false;
        WorldPoint that = (WorldPoint) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}