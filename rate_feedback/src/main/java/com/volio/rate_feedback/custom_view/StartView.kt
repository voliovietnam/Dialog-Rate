package com.volio.rate_feedback.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import com.volio.rate_feedback.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var bigStarChecked: Drawable? = null
    private var bigStarUnchecked: Drawable? = null
    private var starChecked: Drawable? = null
    private var starUnchecked: Drawable? = null
    private var starSize: Float = 1f
    private var bigStarSize: Float = 1f
    private var space: Float = 0f
    private var currentStartIndex = 5
    private val rect = Rect()
    private val mapAnim: HashMap<Int, AnimData> = hashMapOf()
    private val defaultAnim: AnimData = AnimData(scale = 1f)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rateEndAnim: Int = -1
    private var isRunningAnim: Boolean = false

    var onChangeStar: (start: Int) -> Unit = {}


    init {
        for (index in 1 until 4) mapAnim[index] = AnimData()
        mapAnim[0] = AnimData(scale = 1f)
        mapAnim[4] = AnimData(scale = 1f)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StartView,
            0, 0
        ).apply {
            runCatching {
                starChecked = getDrawable(R.styleable.StartView_sv_star_checked)
                starUnchecked = getDrawable(R.styleable.StartView_sv_star_unchecked)
                bigStarChecked = getDrawable(R.styleable.StartView_sv_big_star_checked)
                bigStarUnchecked = getDrawable(R.styleable.StartView_sv_big_star_unchecked)
                starSize = getDimensionPixelSize(R.styleable.StartView_sv_start_size, 40).toFloat()
                bigStarSize =
                    getDimensionPixelSize(R.styleable.StartView_sv_big_start_size, 80).toFloat()
                paint.color = getColor(R.styleable.StartView_sv_color_shadow, Color.TRANSPARENT)
            }


        }.recycle()
        paint.setMaskFilter(
            BlurMaskFilter(
                starSize / 5f /* shadowRadius */,
                BlurMaskFilter.Blur.NORMAL
            )
        );
        startAnim()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        space = (width - starSize / 2f - bigStarSize / 2f - paddingStart - paddingEnd) / 4f

    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            val startX = starSize / 2f + paddingStart
            val halfStar = starSize / 2f
            val halfBigStar = bigStarSize / 2f
            for (index in 0 until 5) {
                val x = startX + index * space
                val animData = mapAnim[index] ?: defaultAnim
                if (index < 4) {
                    rect.setSize(
                        x - halfStar * animData.scale,
                        height / 2f - halfStar * animData.scale,
                        x + halfStar * animData.scale,
                        height / 2f + halfStar * animData.scale
                    )
                    starChecked?.bounds = rect
                    starUnchecked?.bounds = rect
                    canvas.drawCircle(
                        rect.centerX().toFloat(),
                        rect.centerY().toFloat(),
                        halfStar * 1.2f * animData.scaleShadow,
                        paint
                    )
                    if (index > currentStartIndex) {
                        starUnchecked?.draw(canvas)
                    } else {
                        starChecked?.draw(canvas)
                    }

                } else {
                    rect.setSize(
                        x - halfBigStar * animData.scale,
                        height / 2f - halfBigStar * animData.scale,
                        x + halfBigStar * animData.scale,
                        height / 2f + halfBigStar * animData.scale
                    )
                    bigStarChecked?.bounds = rect
                    bigStarUnchecked?.bounds = rect
                    canvas.drawCircle(
                        rect.centerX().toFloat(),
                        rect.centerY().toFloat(),
                        halfBigStar * 1.2f * animData.scaleShadow,
                        paint
                    )
                    if (currentStartIndex >= 4) {
                        bigStarChecked?.draw(canvas)
                    } else {
                        bigStarUnchecked?.draw(canvas)
                    }

                }
            }
        }
    }

    private fun Rect.setSize(left: Float, top: Float, right: Float, bottom: Float) {
        set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                for (index in 0 until 5) {
                    val centerX = paddingStart + starSize / 2f + space * index
                    if (event.x > centerX - space / 2f && event.x < centerX + space / 2f) {
                        if (isRunningAnim) {
                            rateEndAnim = index
                        } else {
                            currentStartIndex = index
                            onChangeStar(currentStartIndex + 1)
                        }
                    }
                }
            }
        }
        invalidate()
        return true
    }

    private var star1: ValueAnimator? = null
    private var star2: ValueAnimator? = null
    private var star3: ValueAnimator? = null
    private var star4: ValueAnimator? = null
    private var star5: ValueAnimator? = null
    private fun ValueAnimator.setStarAnimListener(index: Int) {
        addUpdateListener {
            val value = it.animatedValue as Float
            val animData = mapAnim[index]
            if (animData != null) {
                if (value <= 1f) {
                    if (index != 0 && index != 4) {
                        animData.scale = value
                    } else {
                        animData.scale = 1f
                    }
                    animData.scaleShadow = value
                } else {
                    animData.scaleShadow = 2f - value
                }
            }
            postInvalidate()
        }
    }

    private fun startAnim() {
        star1 = ValueAnimator.ofFloat(0f, 2f).apply {
            setStarAnimListener(0)
            duration = 600
        }
        star2 = ValueAnimator.ofFloat(0f, 2f).apply {
            setStarAnimListener(1)
            duration = 1000
        }
        star3 = ValueAnimator.ofFloat(0f, 2f).apply {
            setStarAnimListener(2)
            duration = 1500
        }
        star4 = ValueAnimator.ofFloat(0f, 2f).apply {
            setStarAnimListener(3)
            duration = 1800
        }
        star5 = ValueAnimator.ofFloat(0f, 2f).apply {
            setStarAnimListener(4)
            doOnEnd {
                isRunningAnim = false
                currentStartIndex = rateEndAnim
                onChangeStar(rateEndAnim+1)
                postInvalidate()
            }
            duration = 2100
        }
        CoroutineScope(Dispatchers.Main).launch {
            isRunningAnim = true
            delay(300)
            star1?.start()
            delay(100)
            star2?.start()
            delay(100)
            star3?.start()
            delay(100)
            star4?.start()
            delay(100)
            star5?.start()
        }
    }

    private data class AnimData(
        var alpha: Float = 1f,
        var rotate: Float = 0f,
        var scale: Float = 0f,
        var scaleShadow: Float = 0f
    )
}