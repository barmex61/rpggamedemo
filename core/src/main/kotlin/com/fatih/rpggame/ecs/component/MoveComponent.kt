package com.fatih.rpggame.ecs.component

import com.fatih.rpggame.utils.Direction

data class MoveComponent(
    var sin : Float = 0f,
    var cos : Float = 0f,
    var speed : Float = 1f,
    var direction: Direction = Direction.RIGHT,
    var root : Boolean = false
){
    companion object{
        const val DEFAULT_SPEED = 1f
    }
}

