package utils;

import engine.data.Vec2;

import java.awt.*;

public class Angles {

    // Coppied and modified from StackExchange
    public static float getAngle(Vec2 from, Vec2 to) {
        float angle = (float) Math.atan2(to.y - from.y, to.x - from.x);

        if((to.y - from.y)<0){
            angle += Math.PI * 2;
        }

        return angle;
    }
}
