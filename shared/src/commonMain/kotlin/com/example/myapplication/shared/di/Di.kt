package com.example.myapplication.shared.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

fun initKoin(appDeclaration: KoinAppDeclaration?) {
    startKoin(
        koinApplication(appDeclaration = appDeclaration)
            .modules(appModule, coroutinesModule),
    )
}
