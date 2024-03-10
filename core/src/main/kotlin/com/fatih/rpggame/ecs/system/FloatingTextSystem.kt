package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.FloatingTextComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier

@AllOf([FloatingTextComponent::class])
class FloatingTextSystem (
    private val floatingTextComps : ComponentMapper<FloatingTextComponent>,
    private val gameStage : Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
): IteratingSystem() {

    private val startPosition = Vector2(0f,0f)

    override fun onTickEntity(entity: Entity) {
        val floatingTextComponent = floatingTextComps[entity]
        floatingTextComponent.run {
            if (remove) world.remove(entity)
            startPosition.set(position)
            gameStage.viewport.project(startPosition)
            uiStage.viewport.unproject(startPosition)
            position.set(
                MathUtils.lerp(position.x,endPosition.x,deltaTime),
                MathUtils.lerp(position.y,endPosition.y,deltaTime),
            )
            damageLabel.setPosition(startPosition.x,uiStage.viewport.worldHeight - startPosition.y)
        }
    }
}
