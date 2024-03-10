package com.fatih.rpggame.ecs.system

import com.fatih.rpggame.ecs.component.StateComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([StateComponent::class])
class StateSystem(
    private val stateComps : ComponentMapper<StateComponent>
) : IteratingSystem(){

    override fun onTickEntity(entity: Entity) {
        val stateComponent = stateComps[entity]
        stateComponent.run {
            if (stateMachine.currentState != currentState){
                stateMachine.changeState(currentState)
            }
            stateMachine.update()
        }

    }

}
