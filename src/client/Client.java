package client;

import client.graphics.GameWindow;
import engine.Camera;
import engine.World;

public class Client {

    public static Client c;

    public World world;
    public Camera camera;

    public static void main(String... args) {
        c = new Client();
    }

    public Client() {
//        world = new World();
//        camera = new Camera();
//        world.start();
//        camera.start();

        GameWindow window = new GameWindow();
        window.world = new World();
        window.world.start();
        window.start();
    }
}
