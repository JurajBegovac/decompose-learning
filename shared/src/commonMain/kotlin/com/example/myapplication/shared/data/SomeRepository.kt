package com.example.myapplication.shared.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SomeRepository {
    fun getValues(): Flow<Int>
}

class SomeRepositoryImpl : SomeRepository {
    override fun getValues(): Flow<Int> =
        flow {
            (1..5000).forEach {
                emit(it)
                delay(1000)
            }
        }
}
