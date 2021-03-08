package com.example.bolavarework.feature.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.bolavarework.data.AuthState
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    val registerState = MutableLiveData<AuthState>(AuthState.EMPTY)
    val loginState = MutableLiveData<AuthState>(AuthState.EMPTY)
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                registerState.postValue(AuthState.SUCCESS)
            }
            else {
                registerState.postValue(AuthState.ERROR(it.exception?.message.toString()))
                Log.d("FIREBASE_REGISTER", it.exception.toString())
            }
        }
    }

    fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) loginState.postValue(AuthState.SUCCESS)
            else {
                loginState.postValue(AuthState.ERROR(it.exception?.message.toString()))
                Log.d("FIREBASE_LOGIN", it.exception.toString())
            }
        }
    }
}