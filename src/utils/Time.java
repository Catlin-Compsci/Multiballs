package utils;

public class Time {

    public static final double NANO_PERIOD = Math.pow(10d, 9d);

    public static long time() {
        return System.nanoTime();
    }
}
