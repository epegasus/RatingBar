package dev.pegasus.bar.views

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IntRange
import com.google.android.material.imageview.ShapeableImageView

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/27/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class PartialView : RelativeLayout {

    private var starWidth = 0
    private var starHeight = 0
    private var sivEmptyView: ShapeableImageView? = null
    private var sivFilledView: ShapeableImageView? = null

    internal constructor(context: Context?, partialViewId: Int, starWidth: Int, startHeight: Int, padding: Int) : super(context) {
        this.tag = partialViewId
        this.starWidth = starWidth
        this.starHeight = startHeight
        setPadding(padding, padding, padding, padding)
        init()
    }

    internal constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    internal constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Make PartialViews use the space when the RatingBar has more width (e.g. match_parent)
        layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)

        val params = LayoutParams(
            if (starWidth == 0) LayoutParams.WRAP_CONTENT else starWidth,
            if (starHeight == 0) LayoutParams.WRAP_CONTENT else starHeight
        )
        params.addRule(CENTER_IN_PARENT)

        sivFilledView = ShapeableImageView(context).also {
            it.scaleType = ImageView.ScaleType.CENTER_CROP
            addView(it, params)
        }
        sivEmptyView = ShapeableImageView(context).also {
            it.scaleType = ImageView.ScaleType.CENTER_CROP
            addView(it, params)
        }
        setEmpty()
    }

    fun setEmpty() {
        sivFilledView?.setImageLevel(0)
        sivEmptyView?.setImageLevel(10000)
    }

    fun setFilled() {
        sivFilledView?.setImageLevel(10000)
        sivEmptyView?.setImageLevel(0)
    }

    fun setFilledDrawable(drawable: Drawable) {
        drawable.constantState?.let {
            val clipDrawable = ClipDrawable(it.newDrawable(), Gravity.START, ClipDrawable.HORIZONTAL)
            sivFilledView?.setImageDrawable(clipDrawable)
        }
    }

    fun setEmptyDrawable(drawable: Drawable) {
        drawable.constantState?.let {
            val clipDrawable = ClipDrawable(it.newDrawable(), Gravity.START, ClipDrawable.HORIZONTAL)
            sivEmptyView?.setImageDrawable(clipDrawable)
        }
    }

    fun setStarWidth(@IntRange(from = 0) starWidth: Int) {
        this.starWidth = starWidth
        val params = sivFilledView?.layoutParams
        params?.width = this.starWidth
        sivFilledView?.layoutParams = params
        sivEmptyView?.layoutParams = params
    }

    fun setStarHeight(@IntRange(from = 0) starHeight: Int) {
        this.starHeight = starHeight
        val params = sivFilledView?.layoutParams
        params?.height = this.starHeight
        sivFilledView?.layoutParams = params
        sivEmptyView?.layoutParams = params
    }

    fun setPartialFilled(rating: Float) {
        val percentage = rating % 1
        var level = (10000 * percentage).toInt()
        level = if (level == 0) 10000 else level
        sivFilledView?.setImageLevel(level)
        sivEmptyView?.setImageLevel(10000 - level)
    }
}