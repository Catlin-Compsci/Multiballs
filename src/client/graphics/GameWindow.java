package client.graphics;

import client.controls.ClientControls;
import engine.World;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    public Canvas canvas;
    public Graphics2D pen;

    public World world;
    public Thread thread;

    public void start() {
        thread.start();
    }


    public GameWindow() {
        super("e");
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Canvas();
        JPanel boi = new JPanel();
        boi.setLayout(new BorderLayout());
        boi.add(canvas, BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(boi, BorderLayout.CENTER);
//        canvas.setSize(new Dimension(100,100));
        canvas.setBackground(Color.WHITE);

        canvas.createBufferStrategy(2);
        pen = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();

        canvas.addMouseListener(ClientControls.getListener());
        canvas.addMouseMotionListener(ClientControls.getListener());
        canvas.addKeyListener(ClientControls.getListener());
        canvas.addMouseWheelListener(ClientControls.getListener());
        canvas.addComponentListener(ClientControls.getListener());

        setSize(new Dimension(1000, 700));

       thread = new Thread(()->{
           while (true) {
               refreshPen();
               pen.setColor(new Color(229, 229, 229));
               pen.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
               pen.translate((canvas.getWidth() - (world.x1-world.x0)*4d/5d)/2,(canvas.getHeight() - (world.y1-world.y0)*4d/5d)/2);
               pen.setColor(Color.WHITE);
               pen.fillRect(0,0,(int)((world.x1-world.x0)*4d/5d),(int)((world.y1-world.y0)*4d/5d));
               pen.setColor(Color.BLACK);
               world.forEach(obj -> {
                   pen.translate(obj.getX()* 4d / 5d,obj.getY()* 4d / 5d);
                   obj.draw(pen);
                   pen.translate(-obj.getX()* 4d / 5d,-obj.getY()* 4d / 5d);
               });
//            pen.fillRect((int) ((ClientControls.mouseScreenPos().x) * 4d / 5d) - 50, (int) ((ClientControls.mouseScreenPos().y) * 4d / 5d) - 50, 100, 100);
               canvas.getBufferStrategy().show();
           }
       });
    }

    private void refreshPen() {
        pen = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
    }
}
