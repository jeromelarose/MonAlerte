package org.jelarose.monalerte

import org.jelarose.monalerte.core.di.iosPlatformModule
import org.koin.core.module.Module

actual fun getPlatformModule(): Module = iosPlatformModule()