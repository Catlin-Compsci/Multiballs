package engine.objects;

import engine.World;

public interface Tickable {
    public default void tick(World world) { }
}
