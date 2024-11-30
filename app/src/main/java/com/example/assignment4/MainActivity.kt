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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get an instance of the database and UserDao
        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()

        setContent {
            Assignment4Theme {
                UserScreen(userDao)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(userDao: UserDao) {
    // State to hold the list of users
    var userList by remember { mutableStateOf(listOf<User>()) }

    // Mutable state variables for user input
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    // Coroutine scope for handling database operations
    val coroutineScope = rememberCoroutineScope()

    // Load users from the database when the screen is first displayed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            userList = withContext(Dispatchers.IO) {
                userDao.getAll() // Fetch users in a background thread
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
                // Input fields for user details
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

                // Button to insert a new user
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newUser = User(name = userName, email = userEmail)
                            withContext(Dispatchers.IO) {
                                userDao.insertAll(newUser) // Insert user in a background thread
                            }
                            userList = withContext(Dispatchers.IO) {
                                userDao.getAll() // Refresh user list
                            }
                        }
                        userName = ""
                        userEmail = ""
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Add User")
                }

                // Display the list of users
                LazyColumn {
                    items(userList) { user -> // Correct `items` function
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${user.name} - ${user.email}")
                            Button(onClick = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        userDao.delete(user) // Delete user in a background thread
                                    }
                                    userList = withContext(Dispatchers.IO) {
                                        userDao.getAll() // Refresh user list
                                    }
                                }
                            }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    )
}
