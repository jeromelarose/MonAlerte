package org.jelarose.monalerte.core.components

import org.jelarose.monalerte.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}