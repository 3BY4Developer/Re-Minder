package com.bitbyter.twodoo.viewmodel


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitbyter.twodoo.data.ToDoDataItem
import com.bitbyter.twodoo.presentation.landing_screen.scheduleReminder
import com.bitbyter.twodoo.presentation.requestExactAlarmPermission
import com.bitbyter.twodoo.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    fun setReminderTime(context: Context, id: String, time: Long, message: String) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            val formattedTime = dateFormat.format(Date(time))
            Log.d("dateCheck", formattedTime)
            repository.addReminderData(id, formattedTime)
            fetchDataItems()

            // Schedule the reminder
            requestExactAlarmPermission(context)
            scheduleReminder(context, id, time, message)
        }
    }

    fun addToDoDataItem(dataItem: ToDoDataItem) {
        viewModelScope.launch {
            repository.addToDoDataItem(dataItem)
            fetchDataItems()
        }
    }

    fun updateToDoDataItem(updatedItem: ToDoDataItem) {
        viewModelScope.launch {
            repository.updateToDoItem(updatedItem)
            fetchDataItems()
        }
    }

    fun deleteDataItem(id: String) {
        viewModelScope.launch {
            repository.deleteDataItem(id)
            fetchDataItems()
        }
    }
}
