package engine;

public class DummyCamera extends Camera {
    @Override
    public void run() {
    }

    @Override
    public double toGameX(double screenX) {
        return screenX;
    }

    @Override
    public double toGameY(double screenY) {
        return screenY;
    }
}
