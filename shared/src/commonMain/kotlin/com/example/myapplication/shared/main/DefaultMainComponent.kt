package com.example.myapplication.shared.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.example.myapplication.shared.collectWithLifecycle
import com.example.myapplication.shared.data.SomeRepository
import com.example.myapplication.shared.getOrCreateMutableState
import kotlinx.coroutines.flow.collectLatest
import org.koin.mp.KoinPlatform.getKoin

class DefaultMainComponent(
    componentContext: ComponentContext,
    someRepository: SomeRepository = getKoin().get(),
    private val onShowWelcome: () -> Unit,
) : ComponentContext by componentContext, MainComponent {

    private val mutableState = instanceKeeper.getOrCreateMutableState(MainComponent.State())

    override val state: Value<MainComponent.State> = mutableState

    init {
        collectWithLifecycle {
            someRepository.getValues()
                .collectLatest { data ->
                    mutableState.update { state ->
                        state.copy(
                            buttonText = data.toString(),
                            buttonEnabled = data > 1,
                        )
                    }
                }
        }
    }

    override fun onShowWelcomeClicked() {
        onShowWelcome()
    }
}
