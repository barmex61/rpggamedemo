package com.fatih.rpggame.ecs.system


import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.TiledComponent
import com.fatih.rpggame.event.CollisionDespawnEvent
import com.fatih.rpggame.event.fireEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    private val tiledComps : ComponentMapper<TiledComponent>,
    private val gameStage : Stage
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val tiledComponent = tiledComps[entity]
        tiledComponent.run {
            if (nearbyEntities.isEmpty()){
                world.remove(entity)
                gameStage.fireEvent(CollisionDespawnEvent(cell))
            }
        }
    }

}
