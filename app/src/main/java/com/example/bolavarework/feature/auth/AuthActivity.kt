package com.example.bolavarework.feature.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.bolavarework.data.User
import com.example.bolavarework.feature.auth.fragments.LoginFragment
import com.example.bolavarework.feature.auth.fragments.RegisterFragment
import com.example.bolavarework.feature.user.UserActivity
import com.example.bolavarework.feature.user.UserViewModel
import com.example.bolavarework.R
import com.example.bolavarework.databinding.ActivityAuthBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel

class AuthActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var firebaseAuth = FirebaseAuth.getInstance()

    private var loginFragment = get<LoginFragment>()
    private val registerFragment = get<RegisterFragment>()
    val viewmodel by viewModel<UserViewModel>() //different viewmodel than 'AUTH' to parse data between them

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragment()
    }

    private fun setupFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, loginFragment)
            addToBackStack("LoginFragment")
            commit()
        }
    }

    fun snackbarMessage(message: String = "Something went wrong...") {
        Snackbar.make(binding.layout, message, Snackbar.LENGTH_SHORT).show()
    }

    fun changeToUserActivity() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            viewmodel.currentUser.postValue(User(currentUser.uid, currentUser.email!!))
            val userIntent = Intent(this, UserActivity::class.java)
            startActivity(userIntent)
            finish()
        } else {
            snackbarMessage()
        }
    }

    override fun onBackPressed() {
        //Leave empty
    }

    fun changeFragment() {
        when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
            "LoginFragment" -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, registerFragment)
                    addToBackStack("RegisterFragment")
                    commit()
                }
            }

            "RegisterFragment" -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, loginFragment)
                    addToBackStack("LoginFragment")
                    commit()
                }
            }

            else -> snackbarMessage()
        }
    }
}