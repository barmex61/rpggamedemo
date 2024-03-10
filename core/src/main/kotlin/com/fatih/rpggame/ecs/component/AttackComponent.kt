package com.fatih.rpggame.ecs.component

import com.fatih.rpggame.utils.AttackState

data class AttackComponent(
    var attackRange : Float = DEFAULT_ATTACK_RANGE,
    var attackDamage : IntRange = (4..6),
    var criticalChance : Int = DEFAULT_CRITICAL_CHANCE,
    var doAttack : Boolean = false,
    var attackState: AttackState = AttackState.READY
){

    fun startAttack(){
        if (doAttack && attackState == AttackState.READY){
            attackState = AttackState.PREPARE
        }
    }

    companion object{
        const val DEFAULT_ATTACK_RANGE = 1f
        const val DEFAULT_CRITICAL_CHANCE = 20
    }
}
