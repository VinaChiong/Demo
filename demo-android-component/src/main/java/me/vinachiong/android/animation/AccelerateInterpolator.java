package me.vinachiong.android.animation;

/**
 * 【手写Animation源码】
 *
 * 加速插值器
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public class AccelerateInterpolator extends BaseInterpolator {
    private float mFactor;
    private double mDoubleFactor;

    AccelerateInterpolator(int factor) {
        this.mFactor = factor;
        this.mDoubleFactor = 2 * mFactor;
    }

    @Override
    public float getInterpolation(float input) {
        if (mFactor == 1) {
            return input * input;
        } else {
            return (float) Math.pow(input, mDoubleFactor);
        }
    }
}
