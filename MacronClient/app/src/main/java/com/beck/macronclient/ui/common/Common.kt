package com.beck.macronclient.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MacronButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal=8.dp, vertical=16.dp),
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldColors(
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledTextColor: Color = Color.White,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    cursorColor: Color = MaterialTheme.colorScheme.secondary,
    errorCursorColor: Color = Color.White,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = Color.Transparent,
) = if(isSystemInDarkTheme()) {
    TextFieldDefaults.outlinedTextFieldColors(
        textColor = textColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        cursorColor = cursorColor,
    )
} else {
    TextFieldDefaults.outlinedTextFieldColors()
}
