package com.example.myapplication.shared

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun initKoin() {
    com.example.myapplication.shared.di.initKoin { }
}

fun initNapier() {
    Napier.base(DebugAntilog())
}
