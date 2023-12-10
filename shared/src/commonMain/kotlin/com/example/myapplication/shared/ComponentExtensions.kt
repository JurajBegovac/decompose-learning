package com.example.myapplication.shared

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import com.example.myapplication.shared.di.qualifierDispatcherDefault
import com.example.myapplication.shared.di.qualifierDispatcherIO
import com.example.myapplication.shared.di.qualifierGlobalScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform.getKoin

val ComponentContext.componentScope: CoroutineScope
    get() = instanceKeeper.getOrCreateScope()

private val ComponentContext.lifecycleFlow: Flow<Lifecycle.State>
    get() = instanceKeeper.getOrCreateLifecycleFlow()

private val mainDispatcher: CoroutineDispatcher
    get() = Dispatchers.Main.immediate // getKoin().get(qualifierDispatcherMainImmediate)

private val ioDispatcher: CoroutineDispatcher
    get() = getKoin().get(qualifierDispatcherIO)

private val defaultDispatcher: CoroutineDispatcher
    get() = getKoin().get(qualifierDispatcherDefault)

private fun componentScopeProvider() = CoroutineScope(SupervisorJob() + mainDispatcher)

private class ScopeHolder : InstanceKeeper.Instance {
    val scope = componentScopeProvider()

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}

private fun InstanceKeeper.getOrCreateScope(): CoroutineScope {
    return getOrCreate { ScopeHolder() }.scope
}

private class LifecycleFlowHolder : InstanceKeeper.Instance {
    val lifecycleFlow = MutableStateFlow(Lifecycle.State.INITIALIZED)
}

private fun InstanceKeeper.getOrCreateLifecycleFlow(): MutableStateFlow<Lifecycle.State> {
    return getOrCreate { LifecycleFlowHolder() }.lifecycleFlow
}

private class MutableStateHolder<State : Any>(initialValue: State) : InstanceKeeper.Instance {
    val mutableState: MutableValue<State> = MutableValue(initialValue)
}

fun <State : Any> InstanceKeeper.getOrCreateMutableState(initialValue: State): MutableValue<State> {
    return getOrCreate { MutableStateHolder(initialValue) }.mutableState
}

typealias Feedback<State, Data, StatePart> = Triple<Flow<Data>, ((Data) -> (StatePart)), ((State, StatePart) -> (State))>

@Suppress("UNCHECKED_CAST")
fun <State : Any, Data : Any, StatePart : Any> feedback(
    dataFlow: Flow<Data>,
    mapper: (Data) -> (StatePart),
    stateReducer: (State, StatePart) -> (State),
) = Feedback(dataFlow, mapper, stateReducer) as Feedback<State, Any, Any>

@OptIn(ExperimentalCoroutinesApi::class)
fun <State : Any> ComponentContext.system(
    initState: State,
    minLifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    feedbacks: Iterable<Feedback<State, Any, Any>>,
): Value<State> {
    if (minLifecycleState == Lifecycle.State.DESTROYED) {
        Napier.d("ViewModel destroyed state called")
        error("minLifecycleState destroyed")
    }
    val mutableState = instanceKeeper.getOrCreateMutableState(initState)
    val mutableLifecycleFlow = instanceKeeper.getOrCreateLifecycleFlow()

    lifecycle.subscribe(
        onCreate = { mutableLifecycleFlow.update { lifecycle.state } },
        onStart = { mutableLifecycleFlow.update { lifecycle.state } },
        onResume = { mutableLifecycleFlow.update { lifecycle.state } },
        onPause = { mutableLifecycleFlow.update { lifecycle.state } },
        onStop = { mutableLifecycleFlow.update { lifecycle.state } },
        onDestroy = { mutableLifecycleFlow.update { lifecycle.state } },
    )

    val feedbacksToCollect =
        feedbacks.map { feedback ->
            feedback.first
                .flowOn(ioDispatcher)
                .mapLatest { feedback.second(it) }
                .flowOn(defaultDispatcher)
                .onEach { data -> mutableState.update { currentState -> feedback.third(currentState, data) } }
                .catch { exception ->
                    Napier.e(
                        throwable = exception,
                        message = "Exception in dataFlow",
                    )
                }
        }.merge()

    componentScope.launch {
        val flowToCollect =
            if (minLifecycleState == Lifecycle.State.INITIALIZED) {
                feedbacksToCollect
            } else {
                lifecycleAwareFlow(minLifecycleState = minLifecycleState, feedbacksToCollect)
            }
        flowToCollect.collect()
    }

    return mutableState
}

@ExperimentalCoroutinesApi
private fun <Data> ComponentContext.lifecycleAwareFlow(
    minLifecycleState: Lifecycle.State,
    dataFlow: Flow<Data>,
): Flow<Data> =
    lifecycleFlow
        .map { it >= minLifecycleState }
        .distinctUntilChanged()
        .flatMapLatest {
            if (it) {
                dataFlow
            } else {
                emptyFlow()
            }
        }

fun ComponentContext.runCommand(block: suspend CoroutineScope.() -> Unit) {
    getKoin().get<CoroutineScope>(qualifierGlobalScope).launch { block() }
}

fun <T> ComponentContext.runUIRelatedCommandWithResult(
    command: suspend CoroutineScope.() -> T,
    onError: (Throwable) -> (Unit) = {},
    onSuccess: (T) -> (Unit),
) {
    componentScope.launch {
        try {
            onSuccess(withContext(ioDispatcher) { command() })
        } catch (e: Exception) {
            onError(e)
        }
    }
}
