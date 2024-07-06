package com.bitbyter.twodoo.presentation.landing_screen

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bitbyter.twodoo.data.ToDoDataItem
import com.bitbyter.twodoo.data.UserData
import com.bitbyter.twodoo.services.ReminderService
import com.bitbyter.twodoo.viewmodel.ToDoDataViewModel
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(userData: UserData?, onSignOut: () -> Unit){
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "2Do",
                    modifier = Modifier.weight(5f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
                )

                if (userData?.profilePictureUrl != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onSignOut() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "sign out"
                )
            }
        },
        modifier = Modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ToDoHome(
    context: Context,
    userData: UserData?,
    toDoViewModel: ToDoDataViewModel,
    onSignOut: () -> Unit
) {
    var isPopup by rememberSaveable { mutableStateOf(false) }
    var toDoTitle by rememberSaveable { mutableStateOf("") }
    var toDoDesc by rememberSaveable { mutableStateOf("") }
    var editingItem by rememberSaveable { mutableStateOf<ToDoDataItem?>(null) }

    Scaffold(
        topBar = { TopBar(userData = userData, onSignOut = onSignOut) },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    toDoTitle = ""
                    toDoDesc = ""
                    isPopup = true
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Add")
                    Icon(Icons.Filled.Add, contentDescription = "Add ToDo")
                }
            }
        },
        content = { innerPadding ->
            ShowToDoList(
                toDoViewModel = toDoViewModel,
                paddingValues = innerPadding,
                onEdit = { item ->
                    editingItem = item
                    toDoTitle = item.title
                    toDoDesc = item.description
                    isPopup = true
                }
            )

            if (isPopup) {
                AddOrEditToDoPopup(
                    toDoTitle = toDoTitle,
                    onTitleChange = { toDoTitle = it },
                    toDoDesc = toDoDesc,
                    onDescChange = { toDoDesc = it },
                    onSave = {
                        if (toDoTitle.isNotEmpty() && toDoDesc.isNotEmpty()) {
                            val newItem =
                                editingItem?.copy(title = toDoTitle, description = toDoDesc)
                            if (newItem != null) {
                                toDoViewModel.updateToDoDataItem(newItem)
                            } else {
                                toDoViewModel.addToDoDataItem(
                                    ToDoDataItem(
                                        title = toDoTitle,
                                        description = toDoDesc
                                    )
                                )
                            }
                            toDoTitle = ""
                            toDoDesc = ""
                        }
                        isPopup = false
                    },
                    onDismiss = { isPopup = false }
                )
            }
        }
    )
}

@Composable
fun AddOrEditToDoPopup(
    toDoTitle: String,
    onTitleChange: (String) -> Unit,
    toDoDesc: String,
    onDescChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding(),  // Use imePadding to adjust for keyboard
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                    }
                }

                TextField(
                    value = toDoTitle,
                    onValueChange = onTitleChange,
                    label = { Text(text = "Title") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onSave() }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = toDoDesc,
                    onValueChange = onDescChange,
                    label = { Text(text = "Description") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onDismiss() }
                    )
                )
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



@Composable
fun ShowToDoList(
    toDoViewModel: ToDoDataViewModel,
    paddingValues: PaddingValues,
    onEdit: (ToDoDataItem) -> Unit
) {
    val context = LocalContext.current
    val dataItems by toDoViewModel.toDoDataItems.collectAsState()
    if (dataItems.isEmpty()) {
        Box(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimationFromUrl("https://lottie.host/ffe72d7d-8a33-416e-b826-5d13e9235506/cJoKnLQhDt.json")
                Text(
                    text = "There are no Remainders. \n       Click Add + button",
                    modifier = Modifier.padding(paddingValues),
                    fontWeight = FontWeight.Bold
                )
            }

        }
    } else {
        var selectedDataItem by remember { mutableStateOf<ToDoDataItem?>(null) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Center)
                ) {
                    dataItems.forEach { dataItem ->
                        var expanded by remember { mutableStateOf(false) }
                        var iconOffset by remember { mutableStateOf(Offset.Zero) }
                        var isChecked by rememberSaveable { mutableStateOf(dataItem.isChecked) }

                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                                .padding(start = 16.dp, top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    isChecked = it
                                    dataItem.isChecked = isChecked
                                    toDoViewModel.updateToDoDataItem(dataItem)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                modifier = Modifier
                                    .weight(7f)
                                    .padding(0.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = dataItem.title,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = dataItem.description,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    maxLines = 2,
                                    fontSize = 15.sp
                                )
                                if (!dataItem.reminderTime.isNullOrBlank()) {
                                    Text(
                                        text = "Reminder: ${dataItem.reminderTime}",
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        fontSize = 15.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            IconButton(
                                onClick = { expanded = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .onGloballyPositioned { coordinates ->
                                        iconOffset = coordinates.positionInWindow()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                offset = with(LocalDensity.current) {
                                    DpOffset(iconOffset.x.toDp(), (-25).dp)
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        expanded = false
                                        onEdit(dataItem)
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        expanded = false
                                        toDoViewModel.deleteDataItem(dataItem.id)
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Add Reminder") },
                                    onClick = {
                                        expanded = false
                                        selectedDataItem = dataItem
                                        showDateTimePicker(context) { timeInMillis ->
                                            val message = dataItem.title
                                            toDoViewModel.setReminderTime(
                                                context,
                                                dataItem.id,
                                                timeInMillis,
                                                message
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

fun showDateTimePicker(context: Context, onDateTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(context, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                onDateTimeSelected(calendar.timeInMillis)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}


@SuppressLint("ScheduleExactAlarm")
fun scheduleReminder(context: Context, itemId: String, timeInMillis: Long, message: String) {
    Log.d(
        "ToDoDataViewModel",
        "Setting reminder for item $itemId at $timeInMillis with message: $message"
    )
    val intent = Intent(context, ReminderService::class.java).apply {
        putExtra("itemId", itemId)
        putExtra("message", message)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        itemId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeInMillis,
        pendingIntent
    )
    Toast.makeText(context, "Reminder set for $message", Toast.LENGTH_SHORT).show()
}

@Composable
fun LottieAnimationFromUrl(url: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier
            .wrapContentSize()
            .height(200.dp)
    )
}