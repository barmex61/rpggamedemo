package com.fatih.rpggame.ecs.component

data class DeadComponent (
    var resurrectionTime : Float = 0f,
    var canResurrect : Boolean = false
    )
