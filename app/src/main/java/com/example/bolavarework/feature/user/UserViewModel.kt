package com.example.bolavarework.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolavarework.data.AuthState
import com.example.bolavarework.data.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    val repository: UserRepository
) : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    val changePasswordState = MutableStateFlow<AuthState>(AuthState.EMPTY)
    val currentUser = MutableLiveData<User>()

    fun changePassword(oldPassword: AuthCredential, newPassword: String) {
        viewModelScope.launch {
            changePasswordState.value = AuthState.LOADING
            repository.changePassword(oldPassword, newPassword)
        }
    }

    fun createUserTestCase() {
        firebaseDatabase.child("ME").setValue("Hi")
    }
}