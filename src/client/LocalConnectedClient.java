package client;

import client.graphics.GameWindow;
import client.networking.ClientNetworker;
import engine.Camera;
import networking.Config;
import networking.ConnectedWorld;
import networking.MessageChunk;
import utils.Random;

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
//        Scanner console = new Scanner(System.in);
//        System.out.println("Bro!!!! Nice to seee you duddddeee!! Ummmmm Errrmmmmmm Wuts ur name again? oopsie doopsie");
//        name = console.nextLine();
        name = Random.r.nextInt(100) + "";

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
