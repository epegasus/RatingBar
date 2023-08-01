package dev.pegasus.bar.states

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * @Author: SOHAIB AHMED
 * @Date: 7/31/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

internal class SavedState : View.BaseSavedState {

    internal var rating = 0f

    constructor(superState: Parcelable?) : super(superState) {
        rating = 0f
    }

    /**
     * Constructor called from [CREATOR]
     */
    private constructor(`in`: Parcel) : super(`in`) {
        rating = `in`.readFloat()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeFloat(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getRating(): Float {
        return rating
    }

    fun setRating(rating: Float) {
        this.rating = rating
    }

    companion object CREATOR : Parcelable.Creator<SavedState> {
        override fun createFromParcel(parcel: Parcel): SavedState {
            return SavedState(parcel)
        }

        override fun newArray(size: Int): Array<SavedState?> {
            return arrayOfNulls(size)
        }
    }
}