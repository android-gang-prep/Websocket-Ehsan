package com.ehsannarmani.socketehsan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ehsannarmani.socketehsan.ui.theme.SocketEhsanTheme

class MainActivity : ComponentActivity() {
    private val viewModel:MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SocketEhsanTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val message = remember {
                        mutableStateOf("")
                    }
                    val isConnected by viewModel.isConnected.collectAsState()
                    val messages by viewModel.messages.collectAsState()
                    val times by viewModel.times.collectAsState()

                    LaunchedEffect(Unit){
                        viewModel.connect()
                        viewModel.startSSE()
                    }

                    Scaffold (topBar = {
                        TopAppBar(title = { Text(text = if (isConnected) "Connected" else "Connecting...") })
                    }){
                        Column(modifier= Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .padding(it)) {
                            OutlinedTextField(value = message.value, onValueChange = {
                                message.value = it
                            },modifier=Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.sendMessage(message.value)
                            },modifier=Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Text(text = "Send")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Messages:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyColumn(modifier=Modifier.fillMaxWidth()){
                                items(messages.reversed()){
                                    Text(text = it)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Time Updates:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyColumn(modifier=Modifier.fillMaxWidth()){
                                items(times.reversed()){
                                    Text(text = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

