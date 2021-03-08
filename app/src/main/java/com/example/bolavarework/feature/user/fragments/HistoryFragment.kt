package com.example.bolavarework.feature.user.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolavarework.feature.user.HistoryAdapter
import com.example.bolavarework.feature.user.UserActivity
import com.example.bolavarework.feature.user.UserViewModel
import com.example.bolavarework.R
import com.example.bolavarework.databinding.FragmentHistoryBinding
import org.koin.android.ext.android.get

class HistoryFragment: Fragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding
    private val viewmodel by activityViewModels<UserViewModel>()
    private lateinit var userActivity: UserActivity

    private val historyAdapter: HistoryAdapter = get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)
        userActivity = activity as UserActivity

        setupRV()
    }

    private fun setupRV() {
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(userActivity)
            adapter = historyAdapter
        }
    }


}