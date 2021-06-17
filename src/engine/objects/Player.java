package engine.objects;

import client.graphics.RenderableObject;
import engine.World;

import java.awt.*;

public class Player extends FrictionBoundedVelocityThing implements RenderableObject {

    public int width = 50;
    public double health = 100;
    public static int MAX_HEALTH = 100;

    public Color color;
//    public Color lighterColor;
    public String name = "";
    public int socketId;

    public double rot = Math.PI/2;

    public Player() {
        this.color = new Color(101, 100, 100, 40);
//        this.color = Random.r.getRandomColor();
    }

    @Override
    public void tick(World world) {
        super.tick(world);
        health = Math.max(0,health);
    }

    @Override
    public void draw(Graphics2D p) {
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
