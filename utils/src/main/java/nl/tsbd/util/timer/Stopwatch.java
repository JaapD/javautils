package nl.tsbd.util.timer;

public class Stopwatch {
    private final long startTime;

    public Stopwatch() {
    this(System.currentTimeMillis());
    }

    Stopwatch(long currentTimeMillis) {
        startTime = currentTimeMillis;
    }

    /**
     * Give the time in miliseconds since start.
     * @return
     */
    public long getTimeMs() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Give the time in seconds since start.
     * @return
     */
    public long getTimeS() {
        return getTimeMs() / 1000;
    }
}
