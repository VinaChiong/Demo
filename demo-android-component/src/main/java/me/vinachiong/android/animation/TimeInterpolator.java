package me.vinachiong.android.animation;

/**
 * 【手写Animation源码】
 *
 * 时间插值器接口：根据动画运行时间，计算出对应的动画百分比
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public interface TimeInterpolator {
    float getInterpolation(float input);
}
