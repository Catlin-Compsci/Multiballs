package client.controls;

import client.Client;
import engine.World;
import engine.data.Vec2;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;

public class ClientControls {
    public static HashMap<Integer, Boolean> keys = new HashMap<>();
    public static HashMap<Integer, LinkedList<Runnable>> onPressedActions = new HashMap<>();
    public static LinkedList<Runnable> onClickedActions = new LinkedList<>();
    private static Point mousePos = new Point(0,0);
    private static boolean mouseDown = false;
    public static Component canvas = null;
    public static World world = null;
    //TODO DELETE
//    static LinkedList<Vec2> bois = new LinkedList<>();

    private static boolean recording = true;
    public static String recordedText = "";

    private static InputListener listener = new InputListener();

    public static void addExecuteWhenMousePressed(Runnable action) {
        onClickedActions.add(action);
    }
    public static void addExecuteWhenPressed(int keyCode, Runnable action) {
        if(!onPressedActions.containsKey(keyCode)) {
            onPressedActions.put(keyCode,new LinkedList<Runnable>());
        }
        onPressedActions.get(keyCode).add(action);
    }
    public static void addExecuteWhenPressed(char key, Runnable action) {
        addExecuteWhenPressed(KeyEvent.getExtendedKeyCodeForChar(key),action);
    }
//    public Vec2 getMouseRawPos() {
//
//    }
//    public Vec2 getMouseWorldPos() {
//
//    }

    public static boolean mousePressed() {
        return mouseDown;
    }

    public static boolean keyPressed(int keyCode) {
        if(keys==null) return false;
        return keys.getOrDefault(keyCode, false);
    }
    public static boolean keyPressed(char c) {
        return keyPressed(KeyEvent.getExtendedKeyCodeForChar(c));
    }

    public static Point mouseScreenPos() {
        return new Point(mousePos.x,mousePos.y);
    }
    public static Vec2 mouseGamePos() {
        if(mousePos == null) return new Vec2(0,0);
//        System.out.println(canvas);
        if(canvas == null) return new Vec2(mousePos.x*5/4,mousePos.y*5/4);
        else return new Vec2((mousePos.x-(canvas.getWidth() - (world.x1-world.x0)*4d/5d)/2)*5d/4d,(mousePos.y-(canvas.getHeight() - (world.y1-world.y0)*4d/5d)/2)*5d/4d);
//        return new Vec2(Client.c.camera.toGameX(mousePos.x),Client.c.camera.toGameY(mousePos.y));
    }

    public static void recordingText(boolean recording) {
        ClientControls.recording = recording;
    }
    public static void resetRecordedText() {
        ClientControls.recordedText = "";
    }

    public static InputListener getListener() {
        return listener;
    }

    private static class InputListener implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener,ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            canvas = e.getComponent();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            canvas = e.getComponent();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            canvas = e.getComponent();
        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            keys.put(e.getKeyCode(),true);
            if(onPressedActions.containsKey(e.getKeyCode())) onPressedActions.get(e.getKeyCode()).forEach(Runnable::run);
            if(recording) recordedText = recordedText + e.getKeyChar();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            keys.put(e.getKeyCode(),false);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
//            bois.add(new Vec2(mouseGamePos().x,mouseGamePos().y));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDown = true;
            onClickedActions.forEach(Runnable::run);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDown = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseDown = true;
            mousePos = e.getPoint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseDown = false;
            mousePos = e.getPoint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }
    }

    private class WhenPressedAction implements KeyListener {

        Runnable action;
        int key;

        public WhenPressedAction(int key, Runnable action) {
            this.key = key;
            this.action = action;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == key) action.run();
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
