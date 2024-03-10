package com.fatih.rpggame.ecs.component

data class LifeComponent (
    var currentLife : Float = 10f,
    var maxLife : Float = DEFAULT_MAX_LIFE,
    var damageTaken : Float = 0f,
    var isCrit : Boolean = false,
    var regeneration : Float = 1f,
    var resurrectionTime : Float = 0f
){

    companion object{
        const val DEFAULT_MAX_LIFE = 10f
    }
}
