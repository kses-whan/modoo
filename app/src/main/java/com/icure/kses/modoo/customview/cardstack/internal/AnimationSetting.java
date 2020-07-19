package com.icure.kses.modoo.customview.cardstack.internal;

import android.view.animation.Interpolator;

import com.icure.kses.modoo.customview.cardstack.Direction;


public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
