package dev.pegasus.bar

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import dev.pegasus.bar.views.AnimationRatingBar
import dev.pegasus.bar.views.PartialView
import kotlin.math.ceil

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/31/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class ScaleRatingBar : AnimationRatingBar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun emptyRatingBar() {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        if (runnable != null) {
            internalHandler.removeCallbacksAndMessages(runnableToken)
        }
        var delay: Long = 0
        for (view in partialViews!!) {
            internalHandler.postDelayed({ view.setEmpty() }, 5.let { delay += it; delay })
        }
    }

    override fun fillRatingBar(rating: Float) {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        if (runnable != null) {
            internalHandler.removeCallbacksAndMessages(runnableToken)
        }
        for (partialView in partialViews!!) {
            val ratingViewId = partialView.tag as Int
            val maxIntOfRating = ceil(rating.toDouble())
            if (ratingViewId > maxIntOfRating) {
                partialView.setEmpty()
                continue
            }
            runnable = getAnimationRunnable(rating, partialView, ratingViewId, maxIntOfRating)
            postRunnable(runnable, ANIMATION_DELAY)
        }
    }

    private fun getAnimationRunnable(rating: Float, partialView: PartialView, ratingViewId: Int, maxIntOfRating: Double): Runnable {
        return Runnable {
            if (ratingViewId.toDouble() == maxIntOfRating) {
                partialView.setPartialFilled(rating)
            } else {
                partialView.setFilled()
            }
            if (ratingViewId.toFloat() == rating) {
                val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                partialView.startAnimation(scaleUp)
                partialView.startAnimation(scaleDown)
            }
        }
    }

    companion object {
        // Control animation speed
        private const val ANIMATION_DELAY: Long = 15
    }
}