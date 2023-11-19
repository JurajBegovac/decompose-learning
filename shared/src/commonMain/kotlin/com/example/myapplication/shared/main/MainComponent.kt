package com.example.myapplication.shared.main

import com.arkivanov.decompose.value.Value

interface MainComponent {

    val model: Value<Model>

    fun onShowWelcomeClicked()

    data class Model(
        val buttonText: String = "",
        val buttonEnabled: Boolean = false
    )
}
