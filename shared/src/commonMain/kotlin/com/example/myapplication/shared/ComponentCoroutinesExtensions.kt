package com.example.myapplication.shared

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun LifecycleOwner.coroutineScope(
    minActiveState: Lifecycle.State = Lifecycle.State.CREATED,
    scopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
): CoroutineScope {
    var scope = scopeProvider()

    if (lifecycle.state == Lifecycle.State.DESTROYED) {
        scope.cancel()
    }

    lifecycle.subscribe(
        onCreate = {
            if (minActiveState == Lifecycle.State.CREATED && scope.isActive.not()) {
                scope = scopeProvider()
            }
        },
        onStart = {
            if (minActiveState == Lifecycle.State.STARTED && scope.isActive.not()) {
                scope = scopeProvider()
            }
        },
        onResume = {
            if (minActiveState == Lifecycle.State.RESUMED && scope.isActive.not()) {
                scope = scopeProvider()
            }
        },
        onPause = {
            if (minActiveState >= Lifecycle.State.RESUMED) {
                scope.cancel()
            }
        },
        onStop = {
            if (minActiveState >= Lifecycle.State.STARTED) {
                scope.cancel()
            }
        },
        onDestroy = {
            scope.cancel()
        },
    )

    return scope
}

fun LifecycleOwner.collectWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    scopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
    block: suspend CoroutineScope.() -> Unit,
) {
    fun launch() {
        coroutineScope(minActiveState, scopeProvider).launch { block(this) }
    }

    when (minActiveState) {
        Lifecycle.State.STARTED -> lifecycle.doOnStart { launch() }
        Lifecycle.State.RESUMED -> lifecycle.doOnResume { launch() }

        Lifecycle.State.DESTROYED,
        Lifecycle.State.INITIALIZED,
        Lifecycle.State.CREATED,
        -> launch()
    }
}

class CoroutineScopeHolder<State : Any>(
    scopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
    initialValue: State,
    block: suspend CoroutineScope.(MutableValue<State>) -> Unit,
) : InstanceKeeper.Instance {

    private val mutableState: MutableValue<State> = MutableValue(initialValue)

    val state: Value<State> get() = mutableState

    val scope =
        scopeProvider().apply {
            launch { block(this, mutableState) }
        }

    override fun onDestroy() {
        scope.cancel()
    }
}

fun <State : Any> InstanceKeeper.getOrCreateCoroutineScopeAndState(
    initialValue: State,
    scopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
    block: suspend CoroutineScope.(MutableValue<State>) -> Unit,
): Value<State> {
    return getOrCreate { CoroutineScopeHolder(scopeProvider, initialValue, block) }.state
}

class MutableStateScopeHolder<State : Any>(initialValue: State) : InstanceKeeper.Instance {
    val mutableState: MutableValue<State> = MutableValue(initialValue)
}

fun <State : Any> InstanceKeeper.getOrCreateMutableState(initialValue: State): MutableValue<State> {
    return getOrCreate { MutableStateScopeHolder(initialValue) }.mutableState
}
