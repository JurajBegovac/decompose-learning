package com.example.myapplication.shared.welcome

import com.arkivanov.decompose.ComponentContext
import com.example.myapplication.shared.getPlatformName

class DefaultWelcomeComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
) : WelcomeComponent(componentContext, State()) {

    override fun onUpdateGreetingText() {
        updateStateBasic { it.copy(greetingText = "Welcome from ${getPlatformName()}") }
    }

    override fun onBackClicked() {
        onFinished()
    }
}
