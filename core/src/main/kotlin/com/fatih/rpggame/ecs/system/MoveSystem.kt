package com.fatih.rpggame.ecs.system

import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.utils.Direction
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([MoveComponent::class])
class MoveSystem(
    private val moveComps : ComponentMapper<MoveComponent>,
    private val physicComps : ComponentMapper<PhysicComponent>,
    private val imageComps : ComponentMapper<ImageComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val moveComponent = moveComps[entity]
        val physicComponent = physicComps[entity]
        val imageComponent = imageComps[entity]
        val velocity = physicComponent.body.linearVelocity
        moveComponent.run {
            val mass = physicComponent.body.mass
            if (root){
                physicComponent.impulse.set(
                    mass * -velocity.x,
                    mass * -velocity.y
                )
                return
            }
            physicComponent.impulse.set(
                mass * cos * speed - velocity.x,
                mass * sin * speed - velocity.y
            )
            if (cos != 0f){
                direction = Direction.RIGHT
                imageComponent.image.flipX = cos < 0f
            }else{
                if (sin < 0f) direction = Direction.FRONT
                if (sin > 0f) direction = Direction.BACK
            }
        }
    }
}
