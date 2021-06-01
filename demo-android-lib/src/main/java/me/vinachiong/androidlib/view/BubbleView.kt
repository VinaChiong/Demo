package me.vinachiong.androidlib.view

import android.animation.ObjectAnimator
import android.animation.PointFEvaluator
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import me.vinachiong.androidlib.R
import kotlin.math.hypot

/**
 * 可拖拽气泡View
 */
class BubbleView : View {
    companion object {
        /** 气泡默认状态--静止 */
        private const val BUBBLE_STATE_DEFAULT = 0
        
        /** 气泡相连 */
        private const val BUBBLE_STATE_CONNECT = 1
        
        /** 气泡分离 */
        private const val BUBBLE_STATE_APART = 2
        
        /** 气泡消失 */
        private const val BUBBLE_STATE_DISMISS = 3
        
        private fun dp2dx(dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
        }
    }
    
    private var mTextContent: String = "12"
    private val mBubblePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint: Paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mTextRect = Rect()
    
    // 自定义变量
    private var mBubbleState: Int = BUBBLE_STATE_DEFAULT
    private var mBubbleDefaultRadius: Float = 0f // 气泡默认半径
        set(value) {
            mMaxDict = 8 * value
            field = value
        }
    
    private var mBubbleFixPoint: PointF = PointF() // 固定气泡的圆心
    private var mBubbleFixRadius: Float = 0f // 固定气泡半径
    private var mBubbleMovablePoint: PointF = PointF() // 动态气泡的圆心
    private var mBubbleMovableRadius: Float = 0f // 可以移动气泡半径
    private val mBezierPath = Path()
    private var mDist: Float = 0f
    
    private var mIsBurstAnimStart = false
    private var mCurrentBurstIndex = 0 // 当前对应的动画
    private var mMaxDict = 0f // 效果的最大距离
    private var mBurstRect = Rect() // 爆炸效果区域
    private var mBurstBitmapsArray: Array<Bitmap>? = null
    private val mBurstDrawablesArray = arrayOf(R.drawable.burst_1,
                                               R.drawable.burst_2,
                                               R.drawable.burst_3,
                                               R.drawable.burst_4,
                                               R.drawable.burst_5)
    
    // 以下是处于 气泡相连 状态下，贝塞尔曲线与 两个气泡的4个切点
    // 分别记录为A, B, C, D
    private val fixCutPointA = PointF()
    private val fixCutPointB = PointF()
    private val movableCutPointC = PointF()
    private val movableCutPointD = PointF()
    
    constructor(context: Context) : super(context) {
        init(null, 0)
    }
    
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }
    
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }
    
    private class Attr(a: TypedArray) {
        //        val mBubbleRadius: Float
    }
    
    private lateinit var mAttr: Attr
    
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.BubbleView, defStyle, 0)
        mAttr = Attr(a)
        a.recycle()
    
        mBubbleDefaultRadius = dp2dx(22f)
        mBubbleFixRadius = mBubbleDefaultRadius
        mBubbleMovableRadius = mBubbleDefaultRadius
        
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = dp2dx(14f)
        
        mBubblePaint.color = Color.RED
        mBubblePaint.style = Paint.Style.FILL
    
        if (mBurstBitmapsArray == null) {
            mBurstBitmapsArray = Array(this.mBurstDrawablesArray.size) {
                BitmapFactory.decodeResource(context.resources, mBurstDrawablesArray[it])
            }
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 重新计算
        mBubbleMovablePoint.set(w / 2f, h / 2f) // 可动圆心位置
        mBubbleFixPoint.set(w / 2f, h / 2f) // 固定圆心位置
    }
    
    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        
        // 气泡没有消失，必须画 可移动圆，
        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            canvas.drawCircle(mBubbleMovablePoint.x, mBubbleMovablePoint.y, mBubbleMovableRadius, mBubblePaint)
            mTextPaint.getTextBounds(mTextContent, 0, mTextContent.length, mTextRect)
            val textX = mBubbleMovablePoint.x - mTextRect.width() / 2
            val textY = mBubbleMovablePoint.y - mTextRect.height() / 2
            canvas.drawText(mTextContent, textX, textY, mTextPaint)
        }
        // 连接状态：手势拉扯，未到达最远距离
        if (mBubbleState == BUBBLE_STATE_CONNECT) {
            // 画固定圆
            canvas.drawCircle(mBubbleFixPoint.x, mBubbleFixPoint.y, mBubbleFixRadius, mBubblePaint)
            
            // 用Path圆弧和填充区域
            // 计算圆弧与两个圆的切点，共4个
            
            val cosXDelta = (mBubbleMovablePoint.x - mBubbleFixPoint.x) / mDist
            val sinXDelta = (mBubbleMovablePoint.y - mBubbleFixPoint.y) / mDist
    
            // 1.计算固定圆的两个切点
            val fixCutPointAx = mBubbleFixPoint.x - mBubbleFixRadius * sinXDelta
            val fixCutPointAy = mBubbleFixPoint.y + mBubbleFixRadius * cosXDelta
            
            val fixCutPointBx = mBubbleFixPoint.x + mBubbleFixRadius * sinXDelta
            val fixCutPointBy = mBubbleFixPoint.y - mBubbleFixRadius * cosXDelta
    
            // 2.计算可动圆的两个切点
            val movableCutPointCx = mBubbleMovablePoint.x - mBubbleMovableRadius * sinXDelta
            val movableCutPointCy = mBubbleMovablePoint.y + mBubbleMovableRadius * cosXDelta
    
            val movableCutPointDx = mBubbleMovablePoint.x + mBubbleMovableRadius * sinXDelta
            val movableCutPointDy = mBubbleMovablePoint.y - mBubbleMovableRadius * cosXDelta
            
            // 4个点确定后，画Path
            val iAnchorX = (mBubbleMovablePoint.x + mBubbleFixPoint.x) / 2f
            val iAnchorY = (mBubbleMovablePoint.y + mBubbleFixPoint.y) / 2f
            
            mBezierPath.reset()
            mBezierPath.moveTo(fixCutPointAx, fixCutPointAy) // 从A点开始画
            mBezierPath.quadTo(iAnchorX, iAnchorY, movableCutPointCx, movableCutPointCy)
            mBezierPath.lineTo(movableCutPointDx, movableCutPointDy)
            mBezierPath.quadTo(iAnchorX, iAnchorY, fixCutPointBx, fixCutPointBy)
            mBezierPath.close()
            canvas.drawPath(mBezierPath, mBubblePaint)
        }
        
        // 已经分离，
        if (mBubbleState == BUBBLE_STATE_APART && null != mBurstBitmapsArray) {
            val left = mBubbleMovablePoint.x - mBubbleMovableRadius
            val top = mBubbleMovablePoint.y - mBubbleMovableRadius
            val right = mBubbleMovablePoint.x + mBubbleMovableRadius
            val bottom = mBubbleMovablePoint.y + mBubbleMovableRadius
            mBurstRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            canvas.drawBitmap(mBurstBitmapsArray!![mCurrentBurstIndex], null, mBurstRect, mBubblePaint)
        }
        
        // 默认绘制Text
        mTextPaint.getTextBounds(mTextContent, 0, mTextContent.length, mTextRect)
        canvas.drawText(mTextContent,
                        mBubbleMovablePoint.x - mTextRect.width() / 2f,
                        mBubbleMovablePoint.y - mTextRect.height() / 2f,
                        mTextPaint)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var touched = false
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mBubbleState != BUBBLE_STATE_DISMISS) {
                    // 判断手指落点是否在圆内
                    mDist = hypot(event.x - mBubbleFixPoint.x, event.y - mBubbleFixPoint.y)
            
                    mBubbleState = if (mDist < mBubbleDefaultRadius) {
                        touched = true
                        BUBBLE_STATE_CONNECT
                    } else {
                        BUBBLE_STATE_DEFAULT
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mBubbleMovablePoint.x = event.x
                mBubbleMovablePoint.y = event.y
                mDist = hypot(event.x - mBubbleFixPoint.x, event.y - mBubbleFixPoint.y)
        
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    if (mDist > mMaxDict) {
                        mBubbleState = BUBBLE_STATE_APART
                    } else {
                        // mDist 接近 mMaxDict ， mBubbleFixRadius 趋向 0f
                        mBubbleFixRadius = mBubbleDefaultRadius - mDist / 8
                    }
                }
                invalidate()
                touched = true
            }
            MotionEvent.ACTION_UP -> {
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    // 松手时候，仍是连接状态，回弹
                    startBubbleInverseAnimation()
                } else if (mBubbleState == BUBBLE_STATE_APART) {
                    // 松手时候，是分离状态
                    if (mDist < 2 * mBubbleDefaultRadius) {
                        // 回弹
                        startBubbleInverseAnimation()
                    } else {
                        // 爆破动画
                        startBubbleBurstAnimator()
                    }
                } else {
                    resetState()
                }
                touched = true
            }
            else -> touched =super.onTouchEvent(event)
        }
        return touched
    }
    
    private fun startBubbleInverseAnimation() {
        val animator = ObjectAnimator.ofObject(PointFEvaluator(),
                                               PointF(mBubbleMovablePoint.x, mBubbleMovablePoint.y),
                                               PointF(mBubbleFixPoint.x, mBubbleFixPoint.y))
        animator.duration = 400
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            mBubbleMovablePoint = it.animatedValue as PointF
            invalidate()
        }
        animator.start()
    }
    
    private fun startBubbleBurstAnimator() {
        mBubbleState = BUBBLE_STATE_DISMISS
        mIsBurstAnimStart = true
        
        val animator = ObjectAnimator.ofInt(0,
                                            mBurstDrawablesArray.size - 1)
        animator.interpolator = LinearInterpolator()
        animator.duration = 2000
        animator.addUpdateListener {
            mCurrentBurstIndex = it.animatedValue as Int
            invalidate()
        }
//        animator.addListener(
//            onStart = {
//                mCurrentBurstIndex = 0
//            },
//            onEnd = {
//            mIsBurstAnimStart = false
//            resetState()
//        })
        animator.start()
    }
    
    private val resetState = true
    private fun resetState() {
        if (resetState) {
            mBubbleState = BUBBLE_STATE_DEFAULT
            mBubbleMovablePoint.x = mBubbleFixPoint.x
            mBubbleMovablePoint.y = mBubbleFixPoint.y
            invalidate()
        }
    }
}