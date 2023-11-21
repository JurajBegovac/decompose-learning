package com.example.myapplication.shared.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.example.myapplication.shared.getPlatformName
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

class DefaultWelcomeComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
) : WelcomeComponent(componentContext) {

    @Composable
    override fun models(events: Flow<Event>): State {
        return WelcomePresenter(events) {
            onFinished()
        }
    }
}

@Composable
fun WelcomePresenter(
    events: Flow<WelcomeComponent.Event>,
    navigateBack: () -> (Unit),
): WelcomeComponent.State {
    var buttonText: String by remember { mutableStateOf("Welcome from Decompose and Molecule!") }

    LaunchedEffect(Unit) {
        events.collect { event ->
            Napier.d { "Collected event: $event" }
            when (event) {
                WelcomeComponent.Event.BackClicked -> {
                    navigateBack()
                }

                WelcomeComponent.Event.ButtonClicked -> {
                    buttonText = "Welcome from ${getPlatformName()}"
                }
            }
        }
    }

    return WelcomeComponent.State(
        greetingText = buttonText,
    )
}
