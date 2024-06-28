package com.bitbyter.twodoo.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbyter.twodoo.data.ToDoDataItem
import com.bitbyter.twodoo.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ToDoDataViewModel(private val repository: FirestoreRepository) : ViewModel() {

    private val _toDoDataItems = MutableStateFlow<List<ToDoDataItem>>(emptyList())
    val toDoDataItems: StateFlow<List<ToDoDataItem>> = _toDoDataItems

    init {
        fetchDataItems()
    }

    private fun fetchDataItems() {
        viewModelScope.launch {
            _toDoDataItems.value = repository.getToDoDataItems()
        }
    }

    fun addtoDoDataItem(dataItem: ToDoDataItem) {
        viewModelScope.launch {
            repository.addToDoDataItem(dataItem)
            fetchDataItems()  // Refresh the list
        }
    }

    fun deleteDataItem(id: String) {
        viewModelScope.launch {
            repository.deleteDataItem(id)
            fetchDataItems()  // Refresh the list
        }
    }
}
