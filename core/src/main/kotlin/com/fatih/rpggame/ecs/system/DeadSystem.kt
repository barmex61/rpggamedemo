package com.fatih.rpggame.ecs.system

import com.fatih.rpggame.ecs.component.DeadComponent
import com.fatih.rpggame.ecs.component.LifeComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([DeadComponent::class])
class DeadSystem(
    private val deadComps : ComponentMapper<DeadComponent>,
    private val lifeComps : ComponentMapper<LifeComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val deadComponent = deadComps[entity]
        deadComponent.run {
            if (!canResurrect){
                world.remove(entity)
                return
            }
            resurrectionTime -= deltaTime
            if (resurrectionTime <= 0f){
                println("resurrect")
                configureEntity(entity){
                    deadComps.remove(it)
                    lifeComps[it].currentLife = lifeComps[it].maxLife
                }
            }
        }
    }
}
