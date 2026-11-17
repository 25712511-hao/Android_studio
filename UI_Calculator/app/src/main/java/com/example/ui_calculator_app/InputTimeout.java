package com.example.ui_calculator_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

public class InputTimeout {

    public static final long DURATION_MS = 10_000L;
    public static final int PROGRESS_MAX = 1000;

    public interface Listener {
        void onProgress(int progress);

        void onExpired();
    }

    private final Listener listener;
    private ValueAnimator animator;
    private boolean cancelled;

    public InputTimeout(Listener listener) {
        this.listener = listener;
    }

    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }

    public void startIfNotRunning() {
        if (isRunning()) return;

        cancelled = false;
        animator = ValueAnimator.ofInt(0, PROGRESS_MAX);
        animator.setDuration(DURATION_MS);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> listener.onProgress((int) a.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                cancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (cancelled) return;
                animator = null;
                listener.onExpired();
            }
        });
        animator.start();
    }
    public void pause() {
        if (animator != null) {
            cancelled = true;
            animator.cancel();
            animator = null;
        }
    }
    public void stop() {
        pause();
        listener.onProgress(0);
    }
}