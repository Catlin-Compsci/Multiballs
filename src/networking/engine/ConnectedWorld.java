package networking.engine;

import client.networking.ClientNetworker;
import engine.World;
import engine.objects.MainPlayer;
import networking.transit.MessageBuilder;
import networking.transit.MessageChunk;
import networking.transit.ServerMessageBuilder;
import server.networking.ServerNetworker;
import utils.Random;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectedWorld extends World {

    public Map<Integer, ConnectedThing> connectedThings = Collections.synchronizedMap(new HashMap<>());
    int lastConnectedId = 0;

    public MainPlayer me;

    public ConnectedWorld(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void startSending() {
        new Thread(() -> {
            if (isClient) {
                while (true) {
                    // Send client position
                    ClientNetworker.active.queue(new MessageBuilder(
                            new MessageChunk.ToServerChunk.ElonMuskWantsToSendHisLocation(me.getId(), me.x, me.xVel, me.y, me.yVel),
                            new MessageChunk.ToServerChunk.ImRotatinHea(me.getId(),me.rot)
                    ));
                    try {
                        Thread.sleep(1000 / 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                while (true) {
                    // Send all player data
                    players.forEach(p -> ServerNetworker.active.queue(new ServerMessageBuilder(p.socketId, true,
                            new MessageChunk.ToServerChunk.ElonMuskWantsToSendHisLocation(
                                    p.getId(), p.x, p.xVel, p.y, p.yVel
                            ),
                            new MessageChunk.ToServerChunk.ImRotatinHea(p.getId(),p.rot)
                    )));

                    if(Random.r.nextInt(10)==1)
                    bullets.forEach(p -> ServerNetworker.active.queue(new ServerMessageBuilder(ServerMessageBuilder.TO_ALL,
                            new MessageChunk.ToServerChunk.ElonMuskWantsToSendHisLocation(
                                    p.getId(), p.x, p.xVel, p.y, p.yVel
                            ))));
                    try {
                        Thread.sleep((int)(1000 / 50 * (players.size()/2d)) * ((bullets.size()+500)/500));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        }).start();
    }

    public MainPlayer makeMe(String name) {
        me = new MainPlayer();
        me.name = name;
        players.add(me);
        return me;
    }

    public int nextId() {
        return lastConnectedId + 1;
    }

    public int addThing(ConnectedThing thing) {
        connectedThings.put(nextId(), thing);
        lastConnectedId++;
        thing.setId(lastConnectedId);
        return lastConnectedId;
    }

    public void putThing(int id, ConnectedThing thing) {
        connectedThings.put(id, thing);
        thing.setId(id);
    }

    public void removeId(int connectedThingId) {
        ConnectedThing thing = connectedThings.get(connectedThingId);
        remove(thing);
        connectedThings.remove(connectedThingId);
    }
}
