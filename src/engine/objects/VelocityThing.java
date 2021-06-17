package engine.objects;

import engine.World;

public class VelocityThing extends ExistentThing{

    public double xVel;
    public double yVel;

    @Override
    public void tick(World world) {
        x += xVel*timeFactor();
        y += yVel*timeFactor();
        super.tick(world);
    }
}
