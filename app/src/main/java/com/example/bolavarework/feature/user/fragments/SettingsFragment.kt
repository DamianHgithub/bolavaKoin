package com.example.bolavarework.feature.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import com.example.bolavarework.data.AuthState
import com.example.bolavarework.feature.user.UserActivity
import com.example.bolavarework.feature.user.UserViewModel
import com.example.bolavarework.R
import com.example.bolavarework.databinding.FragmentSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val viewmodel by activityViewModels<UserViewModel>()
    private lateinit var userActivity: UserActivity
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        userActivity = activity as UserActivity

        initSetup()

        lifecycleScope.launch {
            setupCollectors()
        }

    }

    private suspend fun setupCollectors() {
        viewmodel.repository.state.observe(viewLifecycleOwner, {
            viewmodel.changePasswordState.value = it
            if (it != AuthState.EMPTY) {
                viewmodel.repository.state.postValue(AuthState.EMPTY)
            }
        })

        viewmodel.changePasswordState.collect {
            when (it) {
                AuthState.SUCCESS -> {
                    binding.layoutChangePassword.visibility = View.GONE
                    binding.progressBar.holderProgressBar.animate().apply {
                        duration = resources.getInteger(R.integer.shortAnim).toLong()
                        interpolator = FastOutSlowInInterpolator()
                        alpha(0f)
                        binding.progressBar.holderProgressBar.visibility = View.GONE
                    }
                    userActivity.snackbarMessage("Password updated successfully!")
                    viewmodel.changePasswordState.value = AuthState.EMPTY
                }
                AuthState.LOADING -> {
                    binding.progressBar.holderProgressBar.animate().apply {
                        binding.progressBar.holderProgressBar.visibility = View.VISIBLE
                        duration = resources.getInteger(R.integer.shortAnim).toLong()
                        interpolator = FastOutSlowInInterpolator()
                        alpha(0.8f)
                    }
                    viewmodel.changePasswordState.value = AuthState.EMPTY
                }
                is AuthState.ERROR -> {
                    binding.progressBar.holderProgressBar.animate().apply {
                        duration = resources.getInteger(R.integer.shortAnim).toLong()
                        interpolator = FastOutSlowInInterpolator()
                        alpha(0f)
                        binding.progressBar.holderProgressBar.visibility = View.GONE
                    }
                    userActivity.snackbarMessage(it.exception)
                    viewmodel.changePasswordState.value = AuthState.EMPTY
                }
            }
        }
    }

    private fun initSetup() {
        binding.textUserEmail.text = viewmodel.currentUser.value!!.email

        binding.btnChangePassword.setOnClickListener {
            when (binding.layoutChangePassword.visibility) {
                View.GONE -> {
                    binding.layoutChangePassword.visibility = View.VISIBLE
                    binding.btnCancel.visibility = View.VISIBLE
                }
                View.VISIBLE -> {
                    if (binding.inputNewPassword.text.toString() == binding.inputConfirmPassword.text.toString()) {
                        val user = firebaseAuth.currentUser!!
                        val credentials = EmailAuthProvider.getCredential(user.email.toString(), binding.inputOldPassword.text.toString())
                        viewmodel.changePassword(credentials, binding.inputNewPassword.text.toString())
                    }
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            binding.layoutChangePassword.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
        }
    }
}