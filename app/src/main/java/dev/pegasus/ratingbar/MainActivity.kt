package dev.pegasus.ratingbar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dev.pegasus.bar.BaseRatingBar
import dev.pegasus.bar.interfaces.OnRatingChangeListener
import dev.pegasus.ratingbar.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.scaleRatingBar.setOnRatingChangeListener(rateChangeListener)
        binding.rotationRatingBar.setOnRatingChangeListener(rateChangeListener)
        binding.animationRatingBar.setOnRatingChangeListener(rateChangeListener)
    }

    private val rateChangeListener = object : OnRatingChangeListener {
        override fun onRatingChange(ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean) {
            Log.d("MyTag", "onRatingChange: Rating: $rating")
        }
    }
}