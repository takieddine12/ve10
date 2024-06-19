package com.app.v

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {


    private lateinit var gifImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Handler().postDelayed({
            Intent(this,AdminActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        },6000)
        gifImage = findViewById(R.id.gifImg)
        Glide.with(this)
            .load(R.raw.gif)
            .into(gifImage)

    }


}