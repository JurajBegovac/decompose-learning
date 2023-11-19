package com.example.myapplication.shared.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.example.myapplication.shared.data.SomeRepository
import com.example.myapplication.shared.startCollecting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

class DefaultMainComponent(
    componentContext: ComponentContext,
    mainScopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
    private val ioContext: CoroutineContext = Dispatchers.IO,
    private val someRepository: SomeRepository = getKoin().get(),
    private val onShowWelcome: () -> Unit,
) : MainComponent, ComponentContext by componentContext {

    private val _model = MutableValue(MainComponent.Model())

    override val model: Value<MainComponent.Model> = _model

    init {
        startCollecting(
            minState = Lifecycle.State.STARTED,
            scopeProvider = mainScopeProvider
        ) {
            someRepository.getValues()
                .collect { result ->
                    _model.update {
                        it.copy(
                            buttonText = result.toString(),
                            buttonEnabled = result > 5
                        )
                    }
                }
        }
    }

    override fun onShowWelcomeClicked() {
        onShowWelcome()
    }
}
