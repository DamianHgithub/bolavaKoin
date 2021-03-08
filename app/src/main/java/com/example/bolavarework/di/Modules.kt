package com.example.bolavarework.di

import android.content.Context
import com.example.bolavarework.data.Dialogs
import com.example.bolavarework.data.Permissions
import com.example.bolavarework.feature.auth.AuthActivity
import com.example.bolavarework.feature.auth.AuthRepository
import com.example.bolavarework.feature.auth.AuthViewModel
import com.example.bolavarework.feature.auth.fragments.LoginFragment
import com.example.bolavarework.feature.auth.fragments.RegisterFragment
import com.example.bolavarework.feature.splash.SplashActivity
import com.example.bolavarework.feature.user.HistoryAdapter
import com.example.bolavarework.feature.user.UserRepository
import com.example.bolavarework.feature.user.UserViewModel
import com.example.bolavarework.feature.user.fragments.HistoryFragment
import com.example.bolavarework.feature.user.fragments.MapFragment
import com.example.bolavarework.feature.user.fragments.SettingsFragment
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val fragmentsModule = module {
    factory { LoginFragment() }
    factory { RegisterFragment() }
    factory { HistoryFragment() }
    factory { MapFragment() }
    factory { SettingsFragment() }
}

val utilModule = module {
    factory { (context: Context) -> Dialogs(context) }
    factory { (context: Context) -> Permissions(context) }
}

val adaptersModule = module {
    single { HistoryAdapter() }
}

val activitiesModule = module {
    factory { AuthActivity() }
    factory { SplashActivity() }
}

val architectureModule = module {
    factory { UserRepository() }
    viewModel { UserViewModel(get()) }
    factory { AuthRepository() }
    viewModel { AuthViewModel(get()) }
}