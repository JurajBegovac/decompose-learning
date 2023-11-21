package com.example.myapplication.shared.main

import com.arkivanov.decompose.ComponentContext
import com.example.myapplication.shared.BaseScreenComponent

abstract class MainComponent(
    componentContext: ComponentContext,
    initialState: State,
) : BaseScreenComponent<MainComponent.State>(componentContext, initialState) {

    data class State(
        val buttonText: String = "",
        val buttonEnabled: Boolean = false,
    )

    abstract fun onShowWelcomeClicked()
}
