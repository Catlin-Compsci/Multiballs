package client;

import client.graphics.GameWindow;
import client.networking.ClientNetworker;
import engine.Camera;
import networking.Config;
import networking.engine.ConnectedWorld;
import networking.transit.MessageChunk;
import utils.Random;

import javax.swing.*;

public class LocalConnectedClient {

    public static LocalConnectedClient c;

    public ConnectedWorld world;
    public Camera camera;
    public ClientNetworker networker;

    public static void main(String... args) {
        c = new LocalConnectedClient();
    }

    String name;

    public LocalConnectedClient() {

        // Get username
        name = JOptionPane.showInputDialog("Gimme a name, any name!");
        System.out.println(name);

        // Make & Start World
        world = new ConnectedWorld(true);
        world.makeMe(name);
        world.start();

        // Setup & Start Networking
        networker = new ClientNetworker(world);
        networker.initConnection(Config.localhost,Config.port);
        networker.start();


        // Join
        networker.queue(new MessageChunk.ToServerChunk.Join(name));
        world.startSending();

        // Start Graphics
        GameWindow window = new GameWindow();
        window.world = world;
        window.start();
    }
}
