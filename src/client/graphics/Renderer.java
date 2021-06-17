//package core;// Renders screen
//
//import client.Client;
//import client.controls.ClientControls;
//import game.Camera;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.BufferStrategy;
//import java.util.EventListener;
//import java.util.List;
//
//public class Renderer {
//
//    JFrame window;
//    //    GamePanel screen;
////    Graphics2D pen;
//    Thread thread;
//    Camera c;
//    BufferStrategy renderer;
//
//    public Renderer(Camera c) {
//        this.c = c;
//    }
//
//    public void init() {
//        window = new JFrame();
//        window.setMinimumSize(Const.MINWINDOW);
//        window.setSize(new Dimension(1900,1400));
//
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.addWindowListener(new WindowAdapter() {
//
//            public void windowClosing(WindowEvent e) {
//                Client.e.exit();
//            }
//        });
//
//        window.setVisible(true);
////        c.setDrawComponent(window);
//
//        EventListener l = ClientControls.getListener();
//        window.addKeyListener((KeyListener)l);
//        window.addMouseMotionListener((MouseMotionListener)l);
//        window.addMouseListener((MouseListener)l);
//        window.addMouseWheelListener((MouseWheelListener)l);
//    }
//
//    public void start() {
//        window.createBufferStrategy(2);
//        renderer = window.getBufferStrategy();
////        new Thread(()-> {
//        this.thread = new TickThread(this::tick);
//        this.thread.start();
////        }).start();
//        window.setBackground(Color.WHITE);
//        System.out.println("RENDER THREAD MAKING FINISHED");
//    }
//
//    public void tick() {
////        if(Client.state == AppState.MENU) {
////            System.out.println("REEEEEEEEEEEEEEEEEEEEEEEEEE");
////        }
////        if(Client.state == AppState.GAME) {
////            System.out.println("Starting level tick");
////            Client.e.l.tick();
////            System.out.println("Starting graphics tick");
//            Graphics2D p = (Graphics2D) renderer.getDrawGraphics();
//            p.clearRect(0, 0, window.getWidth(), window.getHeight());
////            draw(Client.e.l.backGraphics, p);
////            draw(Client.e.l.objects, p);
////            draw(Client.e.l.otherPlayers, p);
////            draw(Client.e.l.targetedPlayer, p);
////            draw(Client.e.l.frontGraphics, p);
////            p.setTransform(Const.NO_TRANSFORM);
//            p.setColor(Color.BLACK);
//            p.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
//            p.drawString(ClientControls.mouseGamePos() == null ? "(~,~)" : ClientControls.mouseGamePos().toString(), 20, 80);
//
//            p.dispose();
//            renderer.show();
//        }
//    }
//
//    private void draw(List<? extends GameDrawible> e, Graphics2D p) {
//        e.forEach(object -> {
//            p.setTransform(Const.NO_TRANSFORM);
//            p.scale(1, -1);
//            object.draw(p);
////            System.out.println(object.hb.body.getPosition().y);
//        });
//    }
//    private void draw(GameDrawible object, Graphics2D p) {
//        if(object==null) return;
//        p.setTransform(Const.NO_TRANSFORM);
//        p.scale(1, -1);
//        object.draw(p);
//    }
//}
//
