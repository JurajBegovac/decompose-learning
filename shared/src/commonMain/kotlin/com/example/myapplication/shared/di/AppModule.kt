package com.example.myapplication.shared.di

import com.example.myapplication.shared.data.SomeRepository
import com.example.myapplication.shared.data.SomeRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<SomeRepository> { SomeRepositoryImpl() }
}
