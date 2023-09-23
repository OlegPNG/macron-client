package com.beck.macronclient.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MacronViewModelImpl
import com.beck.macronclient.viewmodel.MockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: MacronViewModel, onLogin: () -> Unit = {}) {
    val email = viewModel.email.collectAsState()

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append("or ")
        }
        pushStringAnnotation(tag = "auth_key", "")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("enter your authentication key")
        }

        pop()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text("Please enter your email")
            TextField(
                value = email.value,
                onValueChange = viewModel::setEmail,
                singleLine = true
            )
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Button(
                    onClick = onLogin,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register")
                }
            }
            ClickableText(text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations(tag="auth_key", start=offset, end=offset).firstOrNull()?.let {
                    Log.d("auth URL", "Enter auth key")
                }
            })
            //Text(annotatedString)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    val viewModel = MockViewModel()
    LoginScreen(viewModel = viewModel)
}