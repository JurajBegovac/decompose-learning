package com.example.myapplication.shared.welcome

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.example.myapplication.shared.getPlatformName

class DefaultWelcomeComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
) : ComponentContext by componentContext, WelcomeComponent {

    private val mutableState = MutableValue(WelcomeComponent.State())

    override val state: Value<WelcomeComponent.State> = mutableState

    override fun onUpdateGreetingText() {
        mutableState.update { it.copy(greetingText = "Welcome from ${getPlatformName()}") }
    }

    override fun onBackClicked() {
        onFinished()
    }
}
