package engine.objects;

import client.controls.ClientControls;
import client.networking.ClientNetworker;
import engine.World;
import engine.data.Vec2;
import networking.transit.MessageChunk;
import utils.Angles;

import java.awt.*;

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
        if (!isOnPoopooBreak && ClientControls.keyPressed(' ')) {
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

    @Override
    public void draw(Graphics2D p) {

        if(!isOnPoopooBreak) {
            super.draw(p);
        } else {
            p.rotate(rot -Math.PI/2);
            p.setColor(new Color(100,100,100, 150));
            p.fillRect(-10,0,20,35);
            p.rotate(-rot +Math.PI/2);

            p.setColor(new Color(141, 141, 141, 107));
//            p.setColor(color);
            p.fillOval(-25,-25,50,50);
        }

//        if (flashTimer.getElapseSeconds() > DYE_TIME)
//            p.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (255 - 190) + (int) (190 * (Math.cos(Math.pow(flashTimer.getElapseSeconds() - DYE_TIME, 2) * Math.PI * 2) + 1) * .5)));
//        else p.setColor(this.color);
//        p.fillOval(-bulletWidth / 2, -bulletWidth / 2, bulletWidth, bulletWidth);
    }
}
