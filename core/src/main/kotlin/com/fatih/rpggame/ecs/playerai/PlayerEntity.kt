package com.fatih.rpggame.ecs.playerai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.fatih.rpggame.ecs.component.AnimationComponent
import com.fatih.rpggame.ecs.component.AnimationComponent.Companion.DEFAULT_FRAME_DURATION
import com.fatih.rpggame.ecs.component.AttackComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.ecs.component.StateComponent
import com.fatih.rpggame.utils.AnimationType
import com.fatih.rpggame.utils.AttackState
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

class PlayerEntity(
    world : World,
    entity: Entity,
    animComps : ComponentMapper<AnimationComponent> = world.mapper(),
    moveComps : ComponentMapper<MoveComponent> = world.mapper(),
    stateComps : ComponentMapper<StateComponent> = world.mapper(),
    attackComps : ComponentMapper<AttackComponent> = world.mapper()
) {

    private val animationComponent = animComps[entity]
    private val moveComponent = moveComps[entity]
    private val stateComponent = stateComps[entity]
    private val attackComponent = attackComps[entity]

    val wantsToAttack : Boolean
        get() = attackComponent.doAttack

    val attackReadyState :Boolean
        get() = attackComponent.attackState == AttackState.READY

    val wantsToRun : Boolean
        get() = moveComponent.cos != 0f || moveComponent.sin != 0f

    val updateDirection : Boolean
        get() = animationComponent.animationType != AnimationType.RUN.animationTypeWithDirection(moveComponent.direction)


    fun animation(animationType: AnimationType,frameDuration : Float = DEFAULT_FRAME_DURATION,playMode: PlayMode = PlayMode.LOOP){
        animationComponent.nextAnimation(animationType.animationTypeWithDirection(moveComponent.direction),frameDuration, playMode)
    }

    fun startAttack(){
        attackComponent.startAttack()
    }

    fun root(enabled : Boolean){
        moveComponent.root = enabled
    }

    fun changeState(state : PlayerState){
        stateComponent.currentState = state
    }

    fun changePreviousState(){
        stateComponent.currentState = stateComponent.stateMachine.previousState
    }
}
