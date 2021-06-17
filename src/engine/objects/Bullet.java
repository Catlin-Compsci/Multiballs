package engine.objects;

import client.graphics.RenderableObject;
import engine.World;
import engine.data.Vec2;
import networking.transit.MessageChunk;
import networking.transit.ServerMessageBuilder;
import server.networking.ServerNetworker;

import java.awt.*;

public class Bullet extends BoundedVelocityThing implements RenderableObject {

    public Player parentPlayer;
    Color color;
    int bulletWidth = 20;

    TimedThing flashTimer = new TimedThing();

    public static double SHOOT_VEL = 200;
    public static double PLAYER_PROP = 0.37;
    public static double DYE_TIME = 5;
    public static double FLASH_TIME = 2;

    public Bullet(Player player) {
        this.x = player.x;
        this.y = player.y;
        this.parentPlayer = player;
        this.color = player.color.brighter().brighter();
    }

    public Bullet(Player player, double xVelNorm, double yVelNorm) {
        this(player);
        this.x += xVelNorm * 40;
        this.y += yVelNorm * 40;
        this.xVel = player.xVel * PLAYER_PROP + xVelNorm * SHOOT_VEL;
        this.yVel = player.yVel * PLAYER_PROP + yVelNorm * SHOOT_VEL;
    }

    @Override
    public void tick(World world) {
        super.tick(world);
        if (!world.isClient) {
            // Do player hit detection
            try {
                for (Player player : world.players) {
                    if (player != parentPlayer && !player.isOnPoopooBreak) {
                        if (new Vec2(player.x - this.x, player.y - this.y).getLength() < this.bulletWidth / 2 + player.width / 2) {
                            // Do player damage
                            if (player.health <= 10 && player.health > 0) {
                                // Knowck player out
                                parentPlayer.score += Player.KO_BONUS;
                                player.score += Player.PENALTY_FOR_GO_POOPOO;
                                player.knockout();
                                ServerNetworker.active.queue(new ServerMessageBuilder(ServerMessageBuilder.TO_ALL,
                                        new MessageChunk.ToClientChunk.PlayerPoopooBreak(player.getId(), true),
                                        new MessageChunk.ToClientChunk.UgggOkBoringNameSetScore(player.getId(), player.score)
                                ));

                            }
                            player.health -= 10;
                            parentPlayer.score += Player.HIT_BONUS;
                            ServerNetworker.active.queue(new ServerMessageBuilder(ServerMessageBuilder.TO_ALL,
                                    new MessageChunk.ToClientChunk.TimeToDIEEEBullet(getId()),
                                    new MessageChunk.ToClientChunk.Health(player.getId(), player.health),
                                    new MessageChunk.ToClientChunk.UgggOkBoringNameSetScore(parentPlayer.getId(), parentPlayer.score)
                            ));
                            world.remove(this);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("ERRRRORRRR");
                e.printStackTrace();
            }
        }
        if (flashTimer.getElapseSeconds() > DYE_TIME + FLASH_TIME) {
            if (!world.isClient)
                ServerNetworker.active.queue(new MessageChunk.ToClientChunk.TimeToDIEEEBullet(getId()));
            world.remove(this);
        }
        ;
    }

    @Override
    public void draw(Graphics2D p) {
//        if(flashTimer.getElapseSeconds() > 3) p.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),4));
        if (flashTimer.getElapseSeconds() > DYE_TIME)
            p.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (255 - 190) + (int) (190 * (Math.cos(Math.pow(flashTimer.getElapseSeconds() - DYE_TIME, 2) * Math.PI * 2) + 1) * .5)));
        else p.setColor(this.color);
        p.fillOval(-bulletWidth / 2, -bulletWidth / 2, bulletWidth, bulletWidth);
    }
}
