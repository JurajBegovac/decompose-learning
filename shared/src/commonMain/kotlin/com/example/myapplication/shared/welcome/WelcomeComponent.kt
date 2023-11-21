package com.example.myapplication.shared.welcome

import com.arkivanov.decompose.ComponentContext
import com.example.myapplication.shared.BaseScreenComponent

abstract class WelcomeComponent(
    componentContext: ComponentContext,
    initialState: State,
) : BaseScreenComponent<WelcomeComponent.State>(
    componentContext,
    initialState,
) {

    data class State(
        val greetingText: String = "Welcome from Decompose!",
    )

    abstract fun onUpdateGreetingText()

    abstract fun onBackClicked()
}
