package engine.objects;

import client.graphics.RenderableObject;
import engine.World;
import networking.transit.MessageChunk;
import server.networking.ServerNetworker;

import java.awt.*;

public class Player extends FrictionBoundedVelocityThing implements RenderableObject {

    public static int HIT_BONUS = 15;
    public static int KO_BONUS = 100;
    public static int PENALTY_FOR_GO_POOPOO = -50;
    public static double POOPOO_BREAK_SECS = 5;
    public boolean isOnPoopooBreak = false;
    public TimedThing knockoutTimer;

    public int width = 50;
    public double health = 100;
    public static int MAX_HEALTH = 100;
    public int score = 0;

    public Color color;
//    public Color lighterColor;
    public String name = "";
    public int socketId;

    public double rot = Math.PI/2;

    public Player() {
        this.color = new Color(101, 100, 100, 40);
        knockoutTimer = new TimedThing();
        knockoutTimer.pause();
//        this.color = Random.r.getRandomColor();
    }

    @Override
    public void tick(World world) {
        super.tick(world);
        health = Math.max(0,health);
        if(!world.isClient) {
            if(isOnPoopooBreak && knockoutTimer.getElapseSeconds() > POOPOO_BREAK_SECS) {
                // Come bacc to game
                respawn();
                ServerNetworker.active.queue(new MessageChunk.ToClientChunk.PlayerPoopooBreak(this.getId(),false));
            }
        }
    }

    public void knockout() {
        isOnPoopooBreak = true;
        knockoutTimer.reset();
        knockoutTimer.resume();
    }

    public void respawn() {
        isOnPoopooBreak = false;
        knockoutTimer.pause();
        knockoutTimer.reset();
        health = MAX_HEALTH;
    }

    @Override
    public void draw(Graphics2D p) {
        if(isOnPoopooBreak) return;
        p.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),140).brighter());
        p.fillRect((int)(-health/2/MAX_HEALTH*70),width/2+4,(int)(health/MAX_HEALTH*70),5);
        p.setColor(new Color(154, 154, 154, 74));
        p.fillRect((int)(health/MAX_HEALTH*70) + (int)(-health/2/MAX_HEALTH*70),width/2+4,(70-(int)(health/MAX_HEALTH*70))/2,5);
        p.fillRect(-70/2,width/2+4,(70-(int)(health/MAX_HEALTH*70))/2+1,5);

        p.rotate(rot -Math.PI/2);
        p.setColor(new Color(100,100,100, 116));
        p.fillRect(-10,0,20,35);
        p.rotate(-rot +Math.PI/2);


        p.setColor(color);
        p.fillOval(-25,-25,50,50);
//        p.drawRect(-25,-25,50,50);
    }
}
