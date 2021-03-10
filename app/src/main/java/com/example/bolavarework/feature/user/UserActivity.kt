package com.example.bolavarework.feature.user

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.bolavarework.data.CONSTS
import com.example.bolavarework.data.Dialogs
import com.example.bolavarework.data.Permissions
import com.example.bolavarework.data.User
import com.example.bolavarework.feature.auth.AuthActivity
import com.example.bolavarework.feature.user.fragments.HistoryFragment
import com.example.bolavarework.feature.user.fragments.MapFragment
import com.example.bolavarework.feature.user.fragments.SettingsFragment
import com.example.bolavarework.R
import com.example.bolavarework.databinding.ActivityUserBinding
import com.example.bolavarework.databinding.NavigationHeaderBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserActivity(): AppCompatActivity() {

    val viewmodel by viewModel<UserViewModel>()
    lateinit var binding: ActivityUserBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    lateinit var header: NavigationHeaderBinding

    private var mapFragment: MapFragment = get()
    private var settingsFragment: SettingsFragment = get()
    private var historyFragment: HistoryFragment = get()
    private val dialogs: Dialogs = get(parameters = { parametersOf(this) })
    private var perms: Permissions = get(parameters = { parametersOf(this) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupFragment()
        viewmodel.createUserCase()
        viewmodel.setHistoryData()

        perms.checkPermissions()
        header = NavigationHeaderBinding.inflate(layoutInflater)
        binding.navigationView.addHeaderView(header.root)

        viewmodel.currentUser.observe(this, {
            if (it == null) signOut()
            else setupDrawer()
        })

        viewmodel.currentUser.postValue(User(firebaseAuth.currentUser!!.uid, firebaseAuth.currentUser!!.email!!))
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), CONSTS.LOCATION_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CONSTS.LOCATION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapFragment.createMap()
        } else if (!shouldShowRequestPermissionRationale(permissions[0])){
            snackbarMessage("You have rejected requested permissions. You'll be redirected to app settings in 3 seconds...")
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }, 3000)
        } else {
            snackbarMessage("You have denied requested permissions. You'll be requested again for permissions in 3 seconds...")
            Handler(Looper.getMainLooper()).postDelayed({
                requestPermissions()
            },3000)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val index = supportFragmentManager.backStackEntryCount - 1
        val currentFragment = supportFragmentManager.getBackStackEntryAt(index).name!!

        when (currentFragment) {
            "SettingsFragment" -> {
                setupFragment()
            }
            "HistoryFragment" -> {
                setupFragment()
            }
            "MapFragment" -> {
                dialogs.confirmLogOut()
            }
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, mapFragment)
            addToBackStack("MapFragment")
            commit()
        }
        binding.drawerlayout.closeDrawers()
        supportActionBar!!.hide()
    }

    private fun changeFragment(changeTo: String) {
        when (changeTo) {
            "SettingsFragment" -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, settingsFragment)
                    addToBackStack("SettingsFragment")
                    commit()
                }
                binding.drawerlayout.closeDrawers()
                supportActionBar!!.apply {
                    title = "Settings"
                    show()
                }
            }
            "HistoryFragment" -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, historyFragment)
                    addToBackStack("HistoryFragment")
                    commit()
                }
                binding.drawerlayout.closeDrawers()
                supportActionBar!!.apply {
                    title = "History"
                    show()
                }
            }
        }
    }

    private fun setupDrawer() {
        val currentUser: User? = viewmodel.currentUser.value
        val currentUsername = currentUser!!.email
        header.username.text = currentUsername

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    changeFragment("SettingsFragment")
                    return@setNavigationItemSelectedListener true
                }
                R.id.history -> {
                    changeFragment("HistoryFragment")
                    return@setNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    dialogs.confirmLogOut()
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener true
            }
        }
    }

    fun snackbarMessage(message: String = "Something went wrong...") {
        Snackbar.make(binding.layout, message, Snackbar.LENGTH_SHORT).show()
    }
}