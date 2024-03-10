package com.fatih.rpggame.ecs.component


import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.fatih.rpggame.ecs.playerai.PlayerEntity
import com.fatih.rpggame.ecs.playerai.PlayerState
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

class StateComponent (
    var currentState : PlayerState = PlayerState.IDLE,
    val stateMachine : DefaultStateMachine<PlayerEntity,PlayerState> = DefaultStateMachine()
){

    companion object{
        class StateComponentListener(val world: World) : ComponentListener<StateComponent>{
            override fun onComponentAdded(entity: Entity, component: StateComponent) {
                component.stateMachine.owner = PlayerEntity(world,entity)
            }

            override fun onComponentRemoved(entity: Entity, component: StateComponent) = Unit
        }
    }
}
