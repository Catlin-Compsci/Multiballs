package engine.objects;

import engine.World;

public class FrictionVelocityThing extends VelocityThing {
    @Override
    public void tick(World world) {
        xVel -= xVel * world.friction * timeFactor();
        yVel -= yVel * world.friction * timeFactor();
//        yVel *= (1 / world.friction * timeFactor());
        super.tick(world);
    }
}
