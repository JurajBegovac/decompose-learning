package com.example.myapplication.shared.welcome

import com.arkivanov.decompose.value.Value

interface WelcomeComponent {

    val state: Value<State>

    data class State(
        val greetingText: String = "Welcome from Decompose!",
    )

    fun onUpdateGreetingText()

    fun onBackClicked()
}
