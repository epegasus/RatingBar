package dev.pegasus.bar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import dev.pegasus.bar.views.PartialView
import dev.pegasus.bar.interfaces.OnRatingChangeListener
import dev.pegasus.bar.interfaces.SimpleRatingBar
import dev.pegasus.bar.states.SavedState
import dev.pegasus.bar.utils.RatingBarUtils

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/27/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

open class BaseRatingBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), SimpleRatingBar {

    private var mStartX = 0f
    private var mStartY = 0f
    private var mRating = -1f
    private var mNumStars = 0
    private var mPadding = 20
    private var mStarWidth = 0
    private var mStarHeight = 0
    private var mMinimumStars = 0f
    private var mIsClickable = true
    private var mPreviousRating = 0f
    private var mEmptyDrawable: Drawable? = null
    private var mFilledDrawable: Drawable? = null
    private var onRatingChangeListener: OnRatingChangeListener? = null

    protected var partialViews: ArrayList<PartialView>? = null

    override var stepSize = 1f
    override var isIndicator = false
    override var isScrollable = true
    override var isClearRatingEnabled = true

    /* Call by xml layout */
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClassicRatingBar)
        val rating = typedArray.getFloat(R.styleable.ClassicRatingBar_crb_rating, 0f)
        initParamsValue(typedArray, context)
        verifyParamsValue()
        initRatingView()
        mRating = rating
    }

    private fun initParamsValue(typedArray: TypedArray, context: Context) {
        mNumStars = typedArray.getInt(R.styleable.ClassicRatingBar_crb_numStars, mNumStars)
        stepSize = typedArray.getFloat(R.styleable.ClassicRatingBar_crb_stepSize, stepSize)
        mMinimumStars = typedArray.getFloat(R.styleable.ClassicRatingBar_crb_minimumStars, mMinimumStars)
        mPadding = typedArray.getDimensionPixelSize(R.styleable.ClassicRatingBar_crb_starPadding, mPadding)
        mStarWidth = typedArray.getDimensionPixelSize(R.styleable.ClassicRatingBar_crb_starWidth, 0)
        mStarHeight = typedArray.getDimensionPixelSize(R.styleable.ClassicRatingBar_crb_starHeight, 0)
        mEmptyDrawable = if (typedArray.hasValue(R.styleable.ClassicRatingBar_crb_drawableEmpty)) ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.ClassicRatingBar_crb_drawableEmpty, NO_ID)) else null
        mFilledDrawable = if (typedArray.hasValue(R.styleable.ClassicRatingBar_crb_drawableFilled)) ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.ClassicRatingBar_crb_drawableFilled, NO_ID)) else null
        isIndicator = typedArray.getBoolean(R.styleable.ClassicRatingBar_crb_isIndicator, isIndicator)
        isScrollable = typedArray.getBoolean(R.styleable.ClassicRatingBar_crb_scrollable, isScrollable)
        mIsClickable = typedArray.getBoolean(R.styleable.ClassicRatingBar_crb_clickable, mIsClickable)
        isClearRatingEnabled = typedArray.getBoolean(R.styleable.ClassicRatingBar_crb_clearRatingEnabled, isClearRatingEnabled)
        typedArray.recycle()
    }

    private fun verifyParamsValue() {
        if (mNumStars <= 0) {
            mNumStars = 5
        }
        if (mPadding < 0) {
            mPadding = 0
        }
        if (mEmptyDrawable == null) {
            mEmptyDrawable = ContextCompat.getDrawable(context, R.drawable.empty)
        }
        if (mFilledDrawable == null) {
            mFilledDrawable = ContextCompat.getDrawable(context, R.drawable.filled)
        }
        if (stepSize > 1.0f) {
            stepSize = 1.0f
        } else if (stepSize < 0.1f) {
            stepSize = 0.1f
        }
        mMinimumStars = RatingBarUtils.getValidMinimumStars(mMinimumStars, mNumStars, stepSize)
    }

    private fun initRatingView() {
        partialViews = ArrayList()
        for (i in 1..mNumStars) {
            val partialView = getPartialView(i, mStarWidth, mStarHeight, mPadding, mFilledDrawable, mEmptyDrawable)
            addView(partialView)
            partialViews?.add(partialView)
        }
    }

    private fun getPartialView(
        partialViewId: Int, starWidth: Int, starHeight: Int, padding: Int,
        filledDrawable: Drawable?, emptyDrawable: Drawable?
    ): PartialView {
        val partialView = PartialView(context, partialViewId, starWidth, starHeight, padding)
        partialView.setFilledDrawable(filledDrawable!!)
        partialView.setEmptyDrawable(emptyDrawable!!)
        return partialView
    }

    /**
     * Retain this method to let other RatingBar can custom their decrease animation.
     */
    protected open fun emptyRatingBar() {
        fillRatingBar(0f)
    }

    /**
     * Use {maxIntOfRating} because if the rating is 3.5
     * the view which id is 3 also need to be filled.
     */
    protected open fun fillRatingBar(rating: Float) {
        for (partialView in partialViews!!) {
            val ratingViewId = partialView.tag as Int
            val maxIntOfRating = Math.ceil(rating.toDouble())
            if (ratingViewId > maxIntOfRating) {
                partialView.setEmpty()
                continue
            }
            if (ratingViewId.toDouble() == maxIntOfRating) {
                partialView.setPartialFilled(rating)
            } else {
                partialView.setFilled()
            }
        }
    }

    override var numStars: Int
        get() = mNumStars
        set(numStars) {
            if (numStars <= 0) {
                return
            }
            partialViews!!.clear()
            removeAllViews()
            mNumStars = numStars
            initRatingView()
        }

    private fun setRating(rating: Float, fromUser: Boolean) {
        var rate = rating
        if (rate > mNumStars) {
            rate = mNumStars.toFloat()
        }
        if (rate < mMinimumStars) {
            rate = mMinimumStars
        }
        if (mRating == rate) {
            return
        }

        // Respect Step size. So if the defined step size is 0.5, and we're attributing it a 4.7 rating,
        // it should actually be set to `4.5` rating.
        val stepAbidingRating = java.lang.Double.valueOf(Math.floor((rate / stepSize).toDouble())).toFloat() * stepSize
        mRating = stepAbidingRating
        if (onRatingChangeListener != null) {
            onRatingChangeListener!!.onRatingChange(this, mRating, fromUser)
        }
        fillRatingBar(mRating)
    }

    override var rating: Float
        get() = mRating
        set(rating) {
            setRating(rating, false)
        }

    override var starWidth: Int
        get() = mStarWidth
        // Unit is pixel
        set(starWidth) {
            mStarWidth = starWidth
            for (partialView in partialViews!!) {
                partialView.setStarWidth(starWidth)
            }
        }

    override var starHeight: Int
        get() = mStarHeight
        // Unit is pixel
        set(starHeight) {
            mStarHeight = starHeight
            for (partialView in partialViews!!) {
                partialView.setStarHeight(starHeight)
            }
        }

    override var starPadding: Int
        get() = mPadding
        set(ratingPadding) {
            if (ratingPadding < 0) {
                return
            }
            mPadding = ratingPadding
            for (partialView in partialViews!!) {
                partialView.setPadding(mPadding, mPadding, mPadding, mPadding)
            }
        }

    override fun setEmptyDrawableRes(@DrawableRes res: Int) {
        val drawable = ContextCompat.getDrawable(context, res)
        drawable?.let { setEmptyDrawable(it) }
    }

    override fun setFilledDrawableRes(@DrawableRes res: Int) {
        val drawable = ContextCompat.getDrawable(context, res)
        drawable?.let { setFilledDrawable(it) }
    }

    override fun setEmptyDrawable(drawable: Drawable) {
        mEmptyDrawable = drawable
        for (partialView in partialViews!!) {
            partialView.setEmptyDrawable(drawable)
        }
    }

    override fun setFilledDrawable(drawable: Drawable) {
        mFilledDrawable = drawable
        for (partialView in partialViews!!) {
            partialView.setFilledDrawable(drawable)
        }
    }

    override fun setMinimumStars(@FloatRange(from = 0.0) minimumStars: Float) {
        mMinimumStars = RatingBarUtils.getValidMinimumStars(minimumStars, mNumStars, stepSize)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isIndicator) {
            return false
        }
        val eventX = event.x
        val eventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = eventX
                mStartY = eventY
                mPreviousRating = mRating
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isScrollable) {
                    return false
                }
                handleMoveEvent(eventX)
            }

            MotionEvent.ACTION_UP -> {
                if (!RatingBarUtils.isClickEvent(mStartX, mStartY, event) || !isClickable) {
                    return false
                }
                handleClickEvent(eventX)
            }
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return true
    }

    private fun handleMoveEvent(eventX: Float) {
        for (partialView in partialViews!!) {
            if (eventX < partialView.width / 10f + mMinimumStars * partialView.width) {
                setRating(mMinimumStars, true)
                return
            }
            if (!isPositionInRatingView(eventX, partialView)) {
                continue
            }
            val rating = RatingBarUtils.calculateRating(partialView, stepSize, eventX)
            if (mRating != rating) {
                setRating(rating, true)
            }
        }
    }

    private fun handleClickEvent(eventX: Float) {
        for (partialView in partialViews!!) {
            if (!isPositionInRatingView(eventX, partialView)) {
                continue
            }
            val rating = if (stepSize == 1f) (partialView.tag as Int).toFloat() else RatingBarUtils.calculateRating(partialView, stepSize, eventX)
            if (mPreviousRating == rating && isClearRatingEnabled) {
                setRating(mMinimumStars, true)
            } else {
                setRating(rating, true)
            }
            break
        }
    }

    private fun isPositionInRatingView(eventX: Float, ratingView: View): Boolean {
        return eventX > ratingView.left && eventX < ratingView.right
    }

    fun setOnRatingChangeListener(onRatingChangeListener: OnRatingChangeListener?) {
        this.onRatingChangeListener = onRatingChangeListener
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.rating = mRating
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        rating = ss.rating
    }
}