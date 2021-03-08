package com.example.bolavarework.data

sealed class AuthState {
    object SUCCESS : AuthState()
    object LOADING : AuthState()
    data class ERROR(val exception: String) : AuthState()
    object EMPTY : AuthState()
}