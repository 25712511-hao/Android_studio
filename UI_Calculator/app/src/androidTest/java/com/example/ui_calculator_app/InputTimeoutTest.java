package com.example.ui_calculator_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
public class InputTimeoutTest {

    private static class Recorder implements InputTimeout.Listener {
        final List<Integer> progress = new CopyOnWriteArrayList<>();
        final AtomicInteger expiredCount = new AtomicInteger();
        final CountDownLatch expiredLatch = new CountDownLatch(1);

        @Override
        public void onProgress(int value) {
            progress.add(value);
        }

        @Override
        public void onExpired() {
            expiredCount.incrementAndGet();
            expiredLatch.countDown();
        }

        int last() {
            return progress.isEmpty() ? -1 : progress.get(progress.size() - 1);
        }
    }

    private Recorder recorder;
    private InputTimeout timeout;

    private static void onMain(Runnable action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action);
    }

    private boolean running() {
        AtomicBoolean result = new AtomicBoolean();
        onMain(() -> result.set(timeout.isRunning()));
        return result.get();
    }

    @Before
    public void setUp() {
        recorder = new Recorder();
        timeout = new InputTimeout(recorder);
    }

    @After
    public void tearDown() {
        onMain(() -> timeout.stop());
    }

    @Test
    public void notRunningByDefault() {
        assertFalse(running());
    }

    @Test
    public void startMakesItRun() {
        onMain(() -> timeout.startIfNotRunning());
        assertTrue(running());
    }

    @Test
    public void progressRisesAndStaysInRange() throws InterruptedException {
        onMain(() -> timeout.startIfNotRunning());
        Thread.sleep(1500);

        assertTrue(recorder.last() > 0);
        int previous = 0;
        for (int value : recorder.progress) {
            assertTrue(value >= previous);
            assertTrue(value <= InputTimeout.PROGRESS_MAX);
            previous = value;
        }
    }

    @Test
    public void expiresAfterDuration() throws InterruptedException {
        onMain(() -> timeout.startIfNotRunning());

        assertTrue(recorder.expiredLatch.await(InputTimeout.DURATION_MS + 3000, TimeUnit.MILLISECONDS));
        assertEquals(1, recorder.expiredCount.get());
        assertEquals(InputTimeout.PROGRESS_MAX, recorder.last());
        assertFalse(running());
    }

    @Test
    public void startWhileRunningDoesNotRestart() throws InterruptedException {
        onMain(() -> timeout.startIfNotRunning());
        Thread.sleep(1500);
        int before = recorder.last();
        int index = recorder.progress.size();

        onMain(() -> timeout.startIfNotRunning());
        Thread.sleep(300);

        for (int i = index; i < recorder.progress.size(); i++) {
            assertTrue(recorder.progress.get(i) >= before);
        }
    }

    @Test
    public void pauseStopsWithoutExpiring() throws InterruptedException {
        onMain(() -> timeout.startIfNotRunning());
        Thread.sleep(500);
        onMain(() -> timeout.pause());

        assertFalse(running());
        int frozen = recorder.last();
        assertTrue(frozen > 0);

        Thread.sleep(800);
        assertEquals(frozen, recorder.last());
        assertEquals(0, recorder.expiredCount.get());
    }

    @Test
    public void stopResetsProgressToZero() throws InterruptedException {
        onMain(() -> timeout.startIfNotRunning());
        Thread.sleep(500);
        onMain(() -> timeout.stop());

        assertEquals(0, recorder.last());
        assertFalse(running());

        Thread.sleep(500);
        assertEquals(0, recorder.last());
        assertEquals(0, recorder.expiredCount.get());
    }

    @Test
    public void canStartAgainAfterPause() throws InterruptedException {
        onMain(() -> timeout.startIfNotRunning());
        Thread.sleep(300);
        onMain(() -> timeout.pause());
        assertFalse(running());

        onMain(() -> timeout.startIfNotRunning());
        assertTrue(running());
    }
}