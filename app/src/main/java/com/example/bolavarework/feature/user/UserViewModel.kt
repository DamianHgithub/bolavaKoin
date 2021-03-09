package com.example.bolavarework.feature.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolavarework.data.AuthState
import com.example.bolavarework.data.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    val repository: UserRepository
) : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val curUser = FirebaseAuth.getInstance().currentUser
    val changePasswordState = MutableStateFlow<AuthState>(AuthState.EMPTY)
    val currentUser = MutableLiveData<User>()

    fun changePassword(oldPassword: AuthCredential, newPassword: String) {
        viewModelScope.launch {
            changePasswordState.value = AuthState.LOADING
            repository.changePassword(oldPassword, newPassword)
        }
    }

    fun createUserCase() {
        firebaseDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.child("users").child(curUser?.uid.toString()).exists()) {
                    firebaseDatabase.child("users").child(curUser?.uid.toString()).apply {
                        child("email").setValue(curUser?.email.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun pushToUsersHistory() {
        firebaseDatabase.child("users").child(curUser?.uid.toString()).child("history").push().setValue("Test")
    }
}