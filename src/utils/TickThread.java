package utils;

public class TickThread extends Thread {
    public TickThread(Runnable target) {
        super(() -> {
            while (true) {
                target.run();
            }
        });
    }
}
