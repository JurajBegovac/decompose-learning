package com.example.myapplication.shared.welcome

import com.arkivanov.decompose.ComponentContext
import com.example.myapplication.shared.MoleculeScreenComponent

abstract class WelcomeComponent(
    componentContext: ComponentContext,
) : MoleculeScreenComponent<WelcomeComponent.Event, WelcomeComponent.State>(componentContext) {
    data class State(val greetingText: String)

    sealed interface Event {
        data object ButtonClicked : Event

        data object BackClicked : Event
    }
}
