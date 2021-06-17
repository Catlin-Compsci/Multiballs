package engine.objects;

import utils.Time;

public class TimedThing {

    public TimedThing() {

    }

    public TimedThing(double seconds) {
        lastTime = System.nanoTime() - (int)(seconds * Time.NANO_PERIOD);
    }

    long lastTime = System.nanoTime();
    long pausedElapse = 0;
    private boolean paused = false;

    public void pause() {
        pausedElapse = getElapse();
        this.paused = true;
    };

    public void resume() {
        lastTime = System.nanoTime() - getElapse();
        this.paused = false;
    };

    public boolean isPaused() {
        return paused;
    }

    public double timeFactor() {
        return (getElapse())/Time.NANO_PERIOD;
    }

    public long getElapse() {
        return paused ? System.nanoTime()-lastTime : System.nanoTime()-lastTime;
    }
    public double getElapseSeconds() {
        return getElapse()/Time.NANO_PERIOD;
    }

    public void reset() {
        pausedElapse = 0;
        lastTime = System.nanoTime();
    }
}
