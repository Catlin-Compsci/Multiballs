package client;

import client.graphics.GameWindow;
import client.networking.ClientNetworker;
import engine.Camera;
import networking.*;
import networking.engine.ConnectedWorld;
import networking.transit.MessageBuilder;
import networking.transit.MessageChunk;

import javax.swing.*;

public class ConnectedClient {

    public static ConnectedClient c;

    public ConnectedWorld world;
    public Camera camera;
    public ClientNetworker networker;

    public static void main(String... args) {
        c = new ConnectedClient();
    }

    String name;

    public ConnectedClient() {
//        Scanner console = new Scanner(System.in);
//        System.out.println("Bro!!!! Nice to seee you duddddeee!! Ummmmm Errrmmmmmm Wuts ur name again? oopsie doopsie");
//        name = console.nextLine();
//        name = Random.r.nextInt(100) + "";

        // Get username
        name = JOptionPane.showInputDialog("Gimme a name, any name!")
                .replaceAll(MessageChunk.del,"")
                .replaceAll(MessageBuilder.del,"");
        System.out.println(name);

        // Make & Start World
        world = new ConnectedWorld(true);
        world.makeMe(name);
        world.start();

        // Setup & Start Networking
        networker = new ClientNetworker(world);
        networker.initConnection(Config.serverIp,Config.port);
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
