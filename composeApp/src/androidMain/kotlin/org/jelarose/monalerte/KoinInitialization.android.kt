package org.jelarose.monalerte

import org.jelarose.monalerte.core.di.androidModule
import org.koin.core.module.Module

actual fun getPlatformModule(): Module = androidModule