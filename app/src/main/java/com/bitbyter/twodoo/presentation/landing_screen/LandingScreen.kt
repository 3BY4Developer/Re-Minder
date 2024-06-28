package com.bitbyter.twodoo.presentation.landing_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.bitbyter.twodoo.data.ToDoDataItem
import com.bitbyter.twodoo.data.UserData
import com.bitbyter.twodoo.viewmodel.ToDoDataViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Icon"
                )
            }
        },
        modifier = Modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ToDoHome(userData: UserData?, toDoViewModel: ToDoDataViewModel, onSignOut: () -> Unit) {
    val dataItems by toDoViewModel.toDoDataItems.collectAsState()
    var isPopup by rememberSaveable {
        mutableStateOf(false)
    }
    var refreshData by rememberSaveable {
        mutableStateOf(false)
    }
    var toDoTitle by rememberSaveable {
        mutableStateOf("")
    }
    var toDoDesc by rememberSaveable {
        mutableStateOf("")
    }
    Scaffold (
        topBar = { TopBar(userData = userData, onSignOut) },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {isPopup = true},
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row (modifier = Modifier.padding(16.dp)){
                    Text(text = "Add")
                    Icon(Icons.Filled.Add, "")
                }
            }
        },
        content = { innerPadding ->
                        ShowToDoList(toDoViewModel, innerPadding)

                if (isPopup){
                    Popup(onDismissRequest = {},
                        properties = PopupProperties(focusable = true),
                        alignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier
                            .heightIn(min = 300.dp, max = 500.dp)
                            .widthIn(min = 200.dp, max = 300.dp)
                            .background(Color.Yellow)) {
                            Column {
                                Row(
                                    modifier = Modifier.padding(16.dp, 0.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(onClick = {
                                        isPopup = false
                                    }) {
                                        Icon(
                                            Icons.Filled.Close,
                                            "",
                                            modifier = Modifier.padding(8.dp),
                                            tint = Color.Gray,
                                        )
                                    }


                                }
                                TextField(
                                    value = toDoTitle,
                                    onValueChange = { toDoTitle = it },
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
                                        onDone = { isPopup = false }
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TextField(
                                    value = toDoDesc,
                                    onValueChange = { toDoDesc = it },
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
                                        onDone = { isPopup = false }
                                    )
                                )
                                Button(
                                    onClick = {
                                        if (toDoTitle.isNotEmpty() && toDoDesc.isNotEmpty()){
                                            toDoViewModel.addtoDoDataItem(ToDoDataItem(title = toDoTitle, description = toDoDesc))
                                            toDoTitle = ""
                                            toDoDesc = ""
                                        }
                                        isPopup = false
                                        refreshData = true
                                    },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(text = "Save")
                                }
                            }
                        }

                    }
                }
            }
    )


}

@Composable
fun ShowToDoList(toDoViewModel: ToDoDataViewModel, paddingValues: PaddingValues){

    val dataItems by toDoViewModel.toDoDataItems.collectAsState()
    Box(modifier = Modifier
        .fillMaxSize()
        .border(2.dp, Color.Blue)
        .padding(paddingValues)) {
                Surface (modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                ){
                    Column(modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Center)) {
                        dataItems.forEach { dataItem ->
                            Row(modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, Color.DarkGray, RectangleShape),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column (modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .weight(7f)
                                    ){
                                    Text(text = dataItem.title,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(16.dp, 8.dp),
                                        fontSize = 20.sp
                                    )
                                    Text(text = dataItem.description,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(8.dp),
                                        maxLines = 2,
                                        fontSize = 15.sp
                                    )
                                    Text(text = dataItem.id,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(8.dp),
                                        maxLines = 2,
                                        fontSize = 15.sp
                                    )
                                }
                                Button(onClick = {toDoViewModel.deleteDataItem(dataItem.id) }, modifier = Modifier.weight(2f).padding(2.dp)) {
                                    Icon(Icons.Filled.Delete, "delete button", modifier = Modifier
                                        , tint = Color.Magenta )
                                }
                            }


                    }
                }



            }
        }
    }

