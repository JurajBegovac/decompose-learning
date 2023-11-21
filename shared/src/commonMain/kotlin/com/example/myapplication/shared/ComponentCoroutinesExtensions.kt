package com.example.myapplication.shared

import com.arkivanov.decompose.ComponentContext
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
    minState: Lifecycle.State = Lifecycle.State.CREATED,
    scopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
): CoroutineScope {
    var scope = scopeProvider()

    if (lifecycle.state == Lifecycle.State.DESTROYED) {
        scope.cancel()
    }

    lifecycle.subscribe(
        onCreate = {
            if (minState == Lifecycle.State.CREATED && scope.isActive.not()) {
                scope = scopeProvider()
            }
        },
        onStart = {
            if (minState == Lifecycle.State.STARTED && scope.isActive.not()) {
                scope = scopeProvider()
            }
        },
        onResume = {
            if (minState == Lifecycle.State.RESUMED && scope.isActive.not()) {
                scope = scopeProvider()
            }
        },
        onPause = {
            if (minState >= Lifecycle.State.RESUMED) {
                scope.cancel()
            }
        },
        onStop = {
            if (minState >= Lifecycle.State.STARTED) {
                scope.cancel()
            }
        },
        onDestroy = {
            scope.cancel()
        },
    )

    return scope
}

fun ComponentContext.startCollecting(
    minState: Lifecycle.State = Lifecycle.State.CREATED,
    scopeProvider: () -> CoroutineScope = { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) },
    coroutineCollectionProvider: suspend CoroutineScope.() -> Unit,
) {
    fun startCollecting() {
        coroutineScope(minState, scopeProvider).launch { coroutineCollectionProvider(this) }
    }

    when (minState) {
        Lifecycle.State.STARTED -> lifecycle.doOnStart { startCollecting() }
        Lifecycle.State.RESUMED -> lifecycle.doOnResume { startCollecting() }

        Lifecycle.State.DESTROYED,
        Lifecycle.State.INITIALIZED,
        Lifecycle.State.CREATED,
        -> startCollecting()
    }
}
