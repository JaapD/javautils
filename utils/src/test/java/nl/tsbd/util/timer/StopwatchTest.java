package nl.tsbd.util.timer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StopwatchTest {
    @Test
    void stopwatchShowsTime() throws InterruptedException {
        Stopwatch stopwatch = new Stopwatch();
        assertThat(stopwatch.getTimeMs()).isLessThan(100);
        assertThat(stopwatch.getTimeS()).isEqualTo(0);

        stopwatch = new Stopwatch(System.currentTimeMillis()-1500);
        Thread.sleep(1);
        assertThat(stopwatch.getTimeMs()).isBetween(1500L, 1600L);
        assertThat(stopwatch.getTimeS()).isEqualTo(1);
    }
}
