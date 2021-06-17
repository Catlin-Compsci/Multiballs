package server;

import networking.Config;
import networking.engine.ConnectedWorld;
import server.networking.ServerNetworker;

public class Server {

    public static void main(String[] args) {
        new Server();
    }

    ConnectedWorld world;
    ServerNetworker networker;

    public Server() {
        world = new ConnectedWorld(false);
        world.start();
        networker = new ServerNetworker(world);
        networker.initServer(Config.port);
        networker.start();
        world.startSending();
    }


}
