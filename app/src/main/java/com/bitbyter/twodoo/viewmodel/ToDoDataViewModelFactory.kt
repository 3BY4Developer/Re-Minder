package com.bitbyter.twodoo.viewmodel

import com.bitbyter.twodoo.repository.FirestoreRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ToDoDataViewModelFactory(private val repository: FirestoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
