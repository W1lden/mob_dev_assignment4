package com.example.assignment4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment4.data.AppDatabase
import com.example.assignment4.data.User
import com.example.assignment4.data.UserDao
import com.example.assignment4.ui.theme.Assignment4Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.items
import com.example.assignment4.data.UserRepository
import com.example.assignment4.network.ApiService
import com.example.assignment4.network.PostRepository
import com.example.assignment4.network.RetrofitInstance
import com.example.assignment4.ui.PostScreen

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val apiService = RetrofitInstance.retrofit.create(ApiService::class.java)
//        val repository = PostRepository(apiService)
//
//        setContent {
//            Assignment4Theme {
//                PostScreen(repository)
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = UserRepository(database.userDao())

        setContent {
            Assignment4Theme {
                UserScreen(repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(repository: UserRepository) {
    var userList by remember { mutableStateOf(listOf<User>()) }
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            userList = withContext(Dispatchers.IO) {
                repository.getAllUsers()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("User Management") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                TextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                TextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newUser = User(name = userName, email = userEmail)
                            withContext(Dispatchers.IO) {
                                repository.addUsers(newUser)
                            }
                            userList = withContext(Dispatchers.IO) {
                                repository.getAllUsers()
                            }
                        }
                        userName = ""
                        userEmail = ""
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Add User")
                }

                LazyColumn {
                    items(userList) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${user.name} - ${user.email}")
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            repository.deleteUser(user)
                                        }
                                        userList = withContext(Dispatchers.IO) {
                                            repository.getAllUsers()
                                        }
                                    }
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    )
}
