package engine.objects;

import client.controls.ClientControls;
import client.networking.ClientNetworker;
import engine.World;
import engine.data.Vec2;
import networking.MessageChunk;
import utils.Angles;

public class MainPlayer extends Player {

    public static double acc = 750;
    TimedThing shootTimer = new TimedThing();
    static double SHOOT_DELAY_SECS = 0.15;

    @Override
    public void tick(World world) {
        if (ClientControls.keyPressed('w')) yVel -= acc * timeFactor();
        if (ClientControls.keyPressed('s')) yVel += acc * timeFactor();
        if (ClientControls.keyPressed('a')) xVel -= acc * timeFactor();
        if (ClientControls.keyPressed('d')) xVel += acc * timeFactor();
        super.tick(world);
        if (ClientControls.keyPressed(' ')) {
            if (shootTimer.getElapseSeconds() > SHOOT_DELAY_SECS) {
                Vec2 dir = new Vec2(ClientControls.mouseGamePos().getX() - getX(), ClientControls.mouseGamePos().getY() - getY());
                dir = dir.getNormalized();

                ClientNetworker.active.queue(new MessageChunk.ToServerChunk.MakeBullet(getId(), dir.x, dir.y));

                shootTimer.reset();
            }
        }
        this.rot = Angles.getAngle(new Vec2(x,y),ClientControls.mouseGamePos());
//        x = ClientControls.mouseScreenPos().getX();
//        y = ClientControls.mouseScreenPos().getY();
    }
}
