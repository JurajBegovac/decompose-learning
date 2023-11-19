package com.example.myapplication.shared.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.example.myapplication.shared.coroutineScope
import com.example.myapplication.shared.data.SomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

class DefaultMainComponent(
    componentContext: ComponentContext,
    mainContext: CoroutineContext = Dispatchers.Main,
    private val ioContext: CoroutineContext = Dispatchers.IO,
    private val someRepository: SomeRepository = getKoin().get(),
    private val onShowWelcome: () -> Unit,
) : MainComponent, ComponentContext by componentContext {

    private val _model = MutableValue(MainComponent.Model())

    override val model: Value<MainComponent.Model> = _model

    private val scope = coroutineScope(mainContext + SupervisorJob())

    init {
        scope.launch {
            someRepository.getValues()
                .collectLatest { result ->
                    _model.update {
                        it.copy(
                            buttonText = result.toString(),
                            buttonEnabled = result > 7
                        )
                    }
                }
        }
    }

    override fun onShowWelcomeClicked() {
        onShowWelcome()
    }
}
