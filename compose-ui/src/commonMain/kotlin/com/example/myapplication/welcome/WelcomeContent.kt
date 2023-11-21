package com.example.myapplication.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.shared.welcome.WelcomeComponent
import io.github.aakira.napier.Napier

@Composable
internal fun WelcomeContent(
    component: WelcomeComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.models.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Welcome Screen") },
                navigationIcon = {
                    IconButton(onClick = { component.take(WelcomeComponent.Event.BackClicked) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back button",
                        )
                    }
                },
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    Napier.d { "Button clicked" }
                    component.take(WelcomeComponent.Event.UpdateGreetingText)
                },
            ) {
                Text(state.greetingText)
            }
        }
    }
}
