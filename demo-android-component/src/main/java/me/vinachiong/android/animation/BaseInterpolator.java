package me.vinachiong.android.animation;

/**
 * 【手写Animation源码】
 *
 * 时间插值器基础实现
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public abstract class BaseInterpolator implements TimeInterpolator {
    private int mChangingConfiguration;

    void setChangingConfiguration(int config) {
        this.mChangingConfiguration = config;
    }

    int getChangingConfiguration() {
        return this.mChangingConfiguration;
    }
}
