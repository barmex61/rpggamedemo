@file:JvmName("Lwjgl3Launcher")

package com.fatih.rpggame.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.fatih.rpggame.RpgGame

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(RpgGame(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("RpgGame")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
