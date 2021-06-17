package engine.objects;

import engine.World;

public class FrictionBoundedVelocityThing extends FrictionVelocityThing {

    public FrictionBoundedVelocityThing() {
    }

    public void tick(World world) {
        if(x <= world.x0 && xVel < 0) xVel *= -1;
        if(y <= world.y0 && yVel < 0) yVel *= -1;
        if(x >= world.x1 && xVel > 0) xVel *= -1;
        if(y >= world.y1 && yVel > 0) yVel *= -1;
        super.tick(world);
    }



}
