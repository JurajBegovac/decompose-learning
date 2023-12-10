package com.example.myapplication.shared.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

val qualifierGlobalScope: Qualifier
    get() = named("globalScope")

val qualifierDispatcherIO: Qualifier
    get() = named("io")

val qualifierDispatcherDefault: Qualifier
    get() = named("default")

val qualifierDispatcherMain: Qualifier
    get() = named("main")

val qualifierDispatcherMainImmediate: Qualifier
    get() = named("mainImmediate")

val coroutinesModule =
    module {

        // app scope
        single(qualifierGlobalScope) { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(qualifierDispatcherIO)) }

        // dispatchers
        factory(qualifierDispatcherIO) { Dispatchers.IO }
        factory(qualifierDispatcherDefault) { Dispatchers.Default }
        factory(qualifierDispatcherMain) { Dispatchers.Main }
        factory(qualifierDispatcherMainImmediate) { Dispatchers.Main.immediate }
    }
