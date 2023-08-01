package dev.pegasus.bar.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import dev.pegasus.bar.BaseRatingBar
import java.util.UUID

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/27/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

abstract class AnimationRatingBar : BaseRatingBar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    protected val internalHandler by lazy { Handler(Looper.getMainLooper()) }
    protected val runnableToken by lazy { UUID.randomUUID().toString() }

    protected var runnable: Runnable? = null

    protected fun postRunnable(runnable: Runnable?, animationDelay: Long) {
        val timeMillis = SystemClock.uptimeMillis() + animationDelay
        runnable?.let {
            internalHandler.postAtTime(it, runnableToken, timeMillis)
        }
    }

    override fun getHandler(): Handler {
        return internalHandler
    }
}