package org.jelarose.monalerte

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform