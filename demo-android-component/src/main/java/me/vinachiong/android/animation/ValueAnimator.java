package me.vinachiong.android.animation;

/**
 * 【手写Animation源码】
 *
 * ValueAnimator：
 * 只仿写动画中，确认插值器的部分的运作
 *
 * 概念：
 * reportCount：循环次数
 * reportMode：循环模式：
 *       - 头尾相接(0->1,0->1,0->1,...)
 *       - 掉头(0->1,1->0,0->1,...)
 * iteration: 第iteration次循环
 * overall fraction: 根据并受限于reportCount 计算得出的总进度数， [0, reportCount+1]
 * current fraction: 当前进度值 [0, overall fraction]
 * current Iteration Fraction: 第iteration次循环的进度分数， [0, 1]
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public class ValueAnimator {
    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation restarts from the beginning.
     */
    public static final int RESTART = 1;
    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation reverses direction on every iteration.
     */
    public static final int REVERSE = 2;
    /**
     * the animation indefinitely.
     */
    public static final int INFINITE = -1;
    static final int NANOS_PER_MS = 1000000;
    float mOverallFraction;
    float mCurrentFraction;
    float mSeekFraction;
    long mDuration = 10 * 1000;
    long mStartTime = -1;
    int mRepeatCount = 0;
    int mRepeatMode = RESTART;
    boolean mReversing = false;

//    TimeInterpolator mInterpolator = new AccelerateInterpolator(2); //单纯加速
    TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator(); // 先加速后减速

    public void start() {
        mStartTime = System.currentTimeMillis();
        System.out.println("##mStartTime = "+ mStartTime );
        new Thread(() -> {
                    int i = 0;
                    boolean finished = false;
                    while (i < mDuration && !finished) {
                        try {
                            Thread.sleep(500);
                            i += 100;
                            long currentTime = System.currentTimeMillis();
                            finished = animateBasedOnTime(currentTime);
                        } catch (InterruptedException e) {

                        }
                    }
                }
        ).start();
    }


    boolean animateBasedOnTime(long currentTime) {
        boolean done = false;

        final long scaledDuration = mDuration;

        final float fraction = (float) (currentTime - mStartTime) / scaledDuration;
        final float lastFraction = mOverallFraction;
        System.out.print("##animateBasedOnTime##:\n fraction ="+ fraction+", lastFraction =" + lastFraction);

        final boolean newIteration = (int) fraction > (int) lastFraction;
        final boolean lastIterationFinished = (fraction >= mRepeatCount + 1)&&
                (mRepeatCount != INFINITE);

        if (scaledDuration == 0) {
            // 0 duration animator, ignore the repeat count and skip to the end
            done = true;
        } else if (newIteration && !lastIterationFinished) {
            // Time to repeat
//            if (mListeners != null) {
//                int numListeners = mListeners.size();
//                for (int i = 0; i < numListeners; ++i) {
//                    mListeners.get(i).onAnimationRepeat(this);
//                }
//            }
        } else if (lastIterationFinished) { // 上一次的迭代，是否属于整个动画进度已结束
            done = true;
        }
        mOverallFraction = clampFraction(fraction);
        System.out.print(" mOverallFraction = " + mOverallFraction + "\n");
        float currentIterationFraction = getCurrentIterationFraction(mOverallFraction, mReversing);
        animateValue(currentIterationFraction);

        return done;
    }

    private static final double FIX =  100.0000000f;

    /**
     *
     * @param fraction 本次循环的进度分数，
     */
    void animateValue(float fraction) {
        System.out.print("      时间分数： " + (fraction * FIX) + "% ");
        fraction = mInterpolator.getInterpolation(fraction);
        System.out.println(" --> 插值器把「时间分数」换算「动画进度分数」 ==> " + (fraction*FIX) + "% ") ;
        mCurrentFraction = fraction;
        // 遍历所有PropertyValuesHolder,调用calculateValue()方法处理
//        int numValues = mValues.length;
//        for (int i = 0; i < numValues; ++i) {
//            mValues[i].calculateValue(fraction);
//        }

        // 回到可以忽略
//        if (mUpdateListeners != null) {
//            int numListeners = mUpdateListeners.size();
//            for (int i = 0; i < numListeners; ++i) {
//                mUpdateListeners.get(i).onAnimationUpdate(this);
//            }
//        }
    }


    /**
     * 把 fraction值拿捏在 [0, mRepeatCount + 1]的范围
     * 如果动画无限循环, 没有范围则没有上边界
     *
     * @param fraction fraction to be clamped
     * @return fraction clamped into the range of [0, mRepeatCount + 1]
     */
    private float clampFraction(float fraction) {
        if (fraction < 0) {
            fraction = 0;
        } else if (mRepeatCount != INFINITE) {
            // 有循环但又不是无限循环时候
            // 保证 fraction 不超过最大分数
            fraction = Math.min(fraction, mRepeatCount + 1);
        }
        return fraction;
    }

    /**
     * Calculates the fraction of the current iteration, taking into account whether the animation
     * should be played backwards. E.g. When the animation is played backwards in an iteration,
     * the fraction for that iteration will go from 1f to 0f.
     * 计算本次循环的动画进度, 需要考虑动画是否反向播放。
     */
    private float getCurrentIterationFraction(float fraction, boolean inReverse) {
        fraction = clampFraction(fraction);
        // 如果是在循环中，要计算出当前在第几次循环；例如  1.5f，则是第2次循环执行中
        int iteration = getCurrentIteration(fraction);
        float currentFraction = fraction - iteration;

        // 如果是 1->0过程： 
        // 否则：
        return shouldPlayBackward(iteration, inReverse) ? 1f - currentFraction : currentFraction;
    }

    // 计算
    private int getCurrentIteration(float fraction) {
        fraction = clampFraction(fraction);
        // If the overall fraction is a positive integer, we consider the current iteration to be
        // complete. In other words, the fraction for the current iteration would be 1, and the
        // current iteration would be overall fraction - 1.
        double iteration = Math.floor(fraction); // 向下取整
        if (fraction == iteration && fraction > 0) {
            iteration--;
        }
        return (int) iteration;
    }

    /**
     * Calculates the direction of animation playing (i.e. forward or backward), based on 1)
     * whether the entire animation is being reversed, 2) repeat mode applied to the current
     * iteration.
     * @param iteration 迭代（循环次数）
     * @param inReverse 当前是否反向播放
     *
     * @return true 表示当前是 1->0， false表示当前是 0->1
     */
    private boolean shouldPlayBackward(int iteration, boolean inReverse) {
        // 循环播放有2种方式：
        //  - 头尾相接(0->1,0->1,0->1,...)
        //  - 掉头(0->1,1->0,0->1,...)
        if (iteration > 0 && mRepeatMode == REVERSE &&
                (iteration < (mRepeatCount + 1) || mRepeatCount == INFINITE)) {
            // 掉头模式且在循环中(  iteration in 0..(mRepeatCount + 1))
            if (inReverse) {
                // 掉头模式，iteration为偶数则属于 1->0过程
                return (iteration % 2) == 0;
            } else {
                // 掉头模式，iteration为基数数则属于 0->1过程
                return (iteration % 2) != 0;
            }
        } else {
            // 头尾相接模式，直接使用inReverse字段判断
            return inReverse;
        }
    }

}
