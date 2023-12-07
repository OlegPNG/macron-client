package com.beck.macronclient.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.beck.macronclient.ui.theme.MacronClientTheme
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: MacronViewModel, onLogin: () -> Unit = {}) {
    val url = viewModel.url.collectAsState().value
    val email = viewModel.email.collectAsState().value
    val password = viewModel.password.collectAsState().value
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            //Text("Enter the URL")
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("URL") },
                value = url,
                onValueChange = viewModel::setUrl,
                singleLine = true,
            )
            //Text("Enter your password")
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                value = email,
                onValueChange = viewModel::setEmail,
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                value = password,
                onValueChange = viewModel::setPassword,
                singleLine = true
            )
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Button(
                    onClick = onLogin,
                    shape = RoundedCornerShape(8.dp),
                    enabled = url != ""
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Preview(
    showSystemUi=true,
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF232634
)
@Composable
fun PreviewLoginScreen() {
    MacronClientTheme {
        val viewModel = MockViewModel()
        LoginScreen(viewModel = viewModel)
    }
}