package dev.pegasus.bar.utils

import android.view.MotionEvent
import dev.pegasus.bar.views.PartialView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/27/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

object RatingBarUtils {

    private var decimalFormat: DecimalFormat? = null
    private const val MAX_CLICK_DISTANCE = 5
    private const val MAX_CLICK_DURATION = 200

    fun isClickEvent(startX: Float, startY: Float, event: MotionEvent): Boolean {
        val duration = (event.eventTime - event.downTime).toFloat()
        if (duration > MAX_CLICK_DURATION) {
            return false
        }
        val differenceX = abs(startX - event.x)
        val differenceY = abs(startY - event.y)
        return !(differenceX > MAX_CLICK_DISTANCE || differenceY > MAX_CLICK_DISTANCE)
    }

    fun calculateRating(partialView: PartialView, stepSize: Float, eventX: Float): Float {
        val decimalFormat = getDecimalFormat()
        val ratioOfView = decimalFormat?.format(((eventX - partialView.left) / partialView.width).toDouble())?.toFloat() ?: 0f
        val steps = (ratioOfView / stepSize).roundToInt() * stepSize
        return decimalFormat?.format((partialView.tag as Int - (1 - steps)).toDouble())?.toFloat() ?: 0f
    }

    fun getValidMinimumStars(minimumStars: Float, numStars: Int, stepSize: Float): Float {
        var minStars = minimumStars
        if (minStars < 0) {
            minStars = 0f
        }
        if (minStars > numStars) {
            minStars = numStars.toFloat()
        }
        if (minStars % stepSize != 0f) {
            minStars = stepSize
        }
        return minStars
    }

    private fun getDecimalFormat(): DecimalFormat? {
        if (decimalFormat == null) {
            val symbols = DecimalFormatSymbols(Locale.ENGLISH)
            symbols.decimalSeparator = '.'
            decimalFormat = DecimalFormat("#.##", symbols)
        }
        return decimalFormat
    }
}