package com.rmnivnv.telegramcontest.views;

import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;

public class DefaultValueAnimator extends ValueAnimator {

    private static final int DEFAULT_ANIMATION_DURATION = 200;

    public DefaultValueAnimator() {
        setDuration(DEFAULT_ANIMATION_DURATION);
        setInterpolator(new AccelerateInterpolator());
    }
}
