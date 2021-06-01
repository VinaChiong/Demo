package me.vinachiong.android.animation;

/**
 * 【手写Animation源码】
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public class AccelerateDecelerateInterpolator extends BaseInterpolator {
    public AccelerateDecelerateInterpolator() {
    }

    public float getInterpolation(float input) {
        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }

}