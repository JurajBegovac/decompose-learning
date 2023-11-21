package com.example.myapplication.shared

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
    minActiveState: Lifecycle.State = Lifecycle.State.CREATED,
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
