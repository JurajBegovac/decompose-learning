package com.example.myapplication.shared

import kotlin.coroutines.CoroutineContext

expect fun getPlatformName(): String

expect fun getMainContext(): CoroutineContext
