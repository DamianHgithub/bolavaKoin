package com.example.bolavarework.feature.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.bolavarework.feature.auth.AuthActivity
import com.example.bolavarework.feature.user.UserActivity
import com.example.bolavarework.R
import com.example.bolavarework.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.android.get

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser

    //private var authActivity: AuthActivity = get()
    //private var userActivity: UserActivity = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animateSplashActivity()
    }

    private fun animateSplashActivity() {
        binding.layout.animate().apply {
            interpolator = FastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.shortAnim).toLong()
            alpha(1f)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser == null) {
                val changeActivity = Intent(this, AuthActivity::class.java)
                startActivity(changeActivity)
                finish()
            } else {
                val changeActivity = Intent(this, UserActivity::class.java)
                startActivity(changeActivity)
                finish()
            }
        }, 3000)
    }

    override fun onBackPressed() {
        finish()
    }
}