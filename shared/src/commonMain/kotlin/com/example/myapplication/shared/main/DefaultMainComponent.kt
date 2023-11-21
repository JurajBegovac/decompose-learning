package com.example.myapplication.shared.main

import com.arkivanov.decompose.ComponentContext
import com.example.myapplication.shared.data.SomeRepository
import org.koin.mp.KoinPlatform.getKoin

class DefaultMainComponent(
    componentContext: ComponentContext,
    someRepository: SomeRepository = getKoin().get(),
    private val onShowWelcome: () -> Unit,
) : MainComponent(componentContext = componentContext, initialState = State()) {

    init {
        updateState(dataFlow = someRepository.getValues()) { state, data ->
            state.copy(
                buttonText = data.toString(),
                buttonEnabled = data > 5,
            )
        }
    }

    override fun onShowWelcomeClicked() {
        onShowWelcome()
    }
}
