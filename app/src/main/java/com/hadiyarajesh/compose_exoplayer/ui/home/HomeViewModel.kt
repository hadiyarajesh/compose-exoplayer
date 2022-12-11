package com.hadiyarajesh.compose_exoplayer.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hadiyarajesh.compose_exoplayer.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    init {
        Log.i(this::class.simpleName, "ViewModel initialized")
    }
}
