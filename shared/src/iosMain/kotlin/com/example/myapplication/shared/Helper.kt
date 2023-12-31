package com.example.myapplication.shared

import com.example.myapplication.shared.di.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

fun initNapier() {
    Napier.base(DebugAntilog())
}
