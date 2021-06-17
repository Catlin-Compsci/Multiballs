package engine.objects;

import networking.engine.ConnectedThing;

public class ExistentThing extends TimedThing implements Tickable, ConnectedThing {

    int id;

    public double x = 100;
    public double y = 100;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
