package com.example.myapplication.shared

import app.cash.molecule.AndroidUiDispatcher
import kotlin.coroutines.CoroutineContext

actual fun getPlatformName(): String = "Android ${android.os.Build.VERSION.SDK_INT}"

actual fun getMainContext(): CoroutineContext = AndroidUiDispatcher.Main
