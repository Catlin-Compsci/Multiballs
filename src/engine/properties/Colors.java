package engine.properties;

import utils.Random;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Colors {
    public static Color[] colors = {
            new Color(167, 46, 46),
            new Color(88, 46, 167),
            new Color(46, 167, 167),
            new Color(69, 172, 10),
    };

    public static ArrayList<Color> freeColors = new ArrayList<>();
    static {
        for (int i = 0; i < colors.length; i++) {
            freeColors.add(colors[i]);
        }
    }

    public static Color takeRandom() {
        Color ret = freeColors.get(Random.r.nextInt(freeColors.size()));
        freeColors.remove(ret);
        return ret;
    }

    public static void recycle(Color colorToBeRecycledLOLLLLL) {
        freeColors.add(colorToBeRecycledLOLLLLL);
    }

    public static int idOf(Color color) {
        for (int i = 0; i < colors.length; i++) {
            if(colors[i].equals(color)) return i;
        }
        return 0;
    }
}
