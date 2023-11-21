package com.example.myapplication.shared

import app.cash.molecule.DisplayLinkClock
import kotlinx.coroutines.Dispatchers
import platform.UIKit.UIDevice
import kotlin.coroutines.CoroutineContext

actual fun getPlatformName(): String = "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"

actual fun getMainContext(): CoroutineContext = Dispatchers.Main + DisplayLinkClock
