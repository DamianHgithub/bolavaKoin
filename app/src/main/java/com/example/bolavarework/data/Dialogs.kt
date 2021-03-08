package com.example.bolavarework.data

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import com.example.bolavarework.feature.user.UserActivity
import com.example.bolavarework.feature.user.UserViewModel
import com.example.bolavarework.databinding.DialogLogoutBinding
import com.example.bolavarework.databinding.DialogPermissionBinding

class Dialogs(
    val context: Context,
) {

    fun confirmLogOut() {
        val builder = AlertDialog.Builder(context)
        val binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        builder.apply {
            setView(binding.root)
        }
        val dialog = builder.create()
        dialog.show()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            (context as UserActivity).viewmodel.currentUser.postValue(null)
            dialog.dismiss()
        }
    }

    fun permissionMessage() {
        val builder = AlertDialog.Builder(context)
        val binding = DialogPermissionBinding.inflate(LayoutInflater.from(context))
        builder.apply {
            setView(binding.root)
        }
        val dialog = builder.create()
        dialog.show()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))

        binding.textOk.setOnClickListener {
            (context as UserActivity).requestPermissions()
            dialog.dismiss()
        }

        binding.textCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}