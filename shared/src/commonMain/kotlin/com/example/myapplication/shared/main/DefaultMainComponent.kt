package com.example.myapplication.shared.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.example.myapplication.shared.componentScope
import com.example.myapplication.shared.data.SomeRepository
import com.example.myapplication.shared.feedback
import com.example.myapplication.shared.system
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import org.koin.mp.KoinPlatform.getKoin

class DefaultMainComponent(
    componentContext: ComponentContext,
    someRepository: SomeRepository = getKoin().get(),
    private val onShowWelcome: () -> Unit,
) : ComponentContext by componentContext, MainComponent {

    private val repoChanges =
        someRepository.getValues()
            .shareIn(scope = componentScope, started = SharingStarted.WhileSubscribed(), replay = 1)

    override val state: Value<MainComponent.State> =
        system(
            initState = MainComponent.State(),
            feedbacks =
                listOf(
                    feedback(
                        dataFlow = repoChanges,
                        mapper = { it.toString() },
                        stateReducer = { currentState, buttonText -> currentState.copy(buttonText = buttonText) },
                    ),
                    feedback(
                        dataFlow = repoChanges,
                        mapper = { it > 2 },
                        stateReducer = { currentState, buttonEnabled -> currentState.copy(buttonEnabled = buttonEnabled) },
                    ),
                ),
        )

    override fun onShowWelcomeClicked() {
        onShowWelcome()
    }
}
