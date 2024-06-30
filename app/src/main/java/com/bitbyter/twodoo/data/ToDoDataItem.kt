package com.bitbyter.twodoo.data

data class ToDoDataItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    var reminderTime: String? = null,
    var isChecked: Boolean = false
)
