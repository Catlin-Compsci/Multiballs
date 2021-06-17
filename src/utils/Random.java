package utils;

import java.awt.*;

public class Random extends java.util.Random {

    public static Random r = new Random();

    static Color[] randomColors = {
            Color.MAGENTA,
            Color.PINK,
            Color.BLUE,
            Color.YELLOW,
            Color.GREEN
    };

    public Color getRandomColor() {
        return randomColors[nextInt(randomColors.length)];
    }

//    public static Color getRandomColor() {
//        return r.getRandomColor();
//    }

}
