package dev.pegasus.bar.interfaces

import dev.pegasus.bar.BaseRatingBar

/**
 * @Author: SOHAIB AHMED
 * @Date: 8/1/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

interface OnRatingChangeListener {
    fun onRatingChange(ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean)
}