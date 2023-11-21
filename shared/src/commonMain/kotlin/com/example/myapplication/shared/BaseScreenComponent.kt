package com.example.myapplication.shared

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

abstract class BaseScreenComponent<State : Any>(
    componentContext: ComponentContext,
    initialState: State,
    private val mainScopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
    private val ioContext: CoroutineContext = Dispatchers.IO,
) : ComponentContext by componentContext {

    private val _state = MutableValue(initialState)

    val state: Value<State> = _state

    protected fun <Data> updateState(
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        dataFlow: Flow<Data>,
        stateReducer: (State, Data) -> State,
    ) {
        collectWithLifecycle(
            minActiveState = minActiveState,
            scopeProvider = mainScopeProvider,
        ) {
            dataFlow.collectLatest { data -> _state.update { currentState -> stateReducer(currentState, data) } }
        }
    }

    // Use it rarely
    protected fun updateStateBasic(stateReducer: (State) -> State) {
        _state.update(stateReducer)
    }
}
