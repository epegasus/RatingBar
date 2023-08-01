package dev.pegasus.bar.interfaces

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/31/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

interface SimpleRatingBar {
    var numStars: Int
    var rating: Float
    var starWidth: Int
    var starHeight: Int
    var starPadding: Int
    var isIndicator: Boolean
    var isScrollable: Boolean
    var isClearRatingEnabled: Boolean
    var stepSize: Float
    fun setEmptyDrawable(drawable: Drawable)
    fun setEmptyDrawableRes(@DrawableRes res: Int)
    fun setFilledDrawable(drawable: Drawable)
    fun setFilledDrawableRes(@DrawableRes res: Int)
    fun setMinimumStars(@FloatRange(from = 0.0) minimumStars: Float)
}