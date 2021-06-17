package engine;

import client.Client;
import client.controls.ClientControls;
import client.graphics.RenderableObject;
import engine.objects.Bullet;
import engine.objects.ExistentThing;
import engine.objects.MainPlayer;
import engine.objects.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class World extends Thread {

    public boolean isClient = false;

    public World() {
        ClientControls.world = this;
    }

    @Override
    public void run() {
        while(true) {
            tick();
        }
    }

    public List<Player> players = Collections.synchronizedList(new LinkedList<>());
    public List<Bullet> bullets = Collections.synchronizedList(new LinkedList<>());
    int groundY = 0;
    public double friction = 0.3;

    public double x0 = 0;
    public double y0 = 0;
    public double x1 = 1000;
    public double y1 = 700;

    public void tick() {
        players.forEach(p->{
            p.pause();
            p.tick(this);
            p.reset();
            p.resume();
        });
        LinkedList<Bullet> test = new LinkedList<>();
        bullets.forEach(b->test.add(b));
        test.forEach(p->{
            p.pause();
            p.tick(this);
            p.reset();
            p.resume();
        });
    }

    public void forEach(Consumer<RenderableObject> doo) {
        bullets.forEach(doo);
        players.forEach(doo);
    }

    public void remove(Object thing) {
        System.out.println(players.remove(thing));
        System.out.println(bullets.remove(thing));
    }
}
