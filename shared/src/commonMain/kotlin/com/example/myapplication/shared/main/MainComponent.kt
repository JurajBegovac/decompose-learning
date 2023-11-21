package com.example.myapplication.shared.main

import com.arkivanov.decompose.value.Value

interface MainComponent {

    val state: Value<State>

    data class State(
        val buttonText: String = "",
        val buttonEnabled: Boolean = false,
    )

    fun onShowWelcomeClicked()
}
