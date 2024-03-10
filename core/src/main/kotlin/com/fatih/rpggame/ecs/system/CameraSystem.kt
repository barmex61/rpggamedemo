package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.tiled.height
import ktx.tiled.width
import kotlin.math.max
import kotlin.math.min

@AllOf([PlayerComponent::class])
class CameraSystem (
    gameStage : Stage,
    private val imageComps : ComponentMapper<ImageComponent>,

): IteratingSystem() , EventListener {

    private val gameCamera = gameStage.camera as OrthographicCamera

    private var maxWidth = gameCamera.viewportWidth /2f
    private var maxHeight = gameCamera.viewportHeight /2f

    override fun onTickEntity(entity: Entity) {
        val imageComponent = imageComps[entity]
        gameCamera.position.set(
            imageComponent.image.x.coerceIn(
                min(gameCamera.viewportWidth/2f,maxWidth - gameCamera.viewportWidth/2f),
                max(gameCamera.viewportWidth/2f,maxWidth - gameCamera.viewportWidth/2f),
            ),
            imageComponent.image.y.coerceIn(
                min(gameCamera.viewportHeight/2f,maxHeight-gameCamera.viewportHeight/2f),
                max(gameCamera.viewportHeight/2f,maxHeight-gameCamera.viewportHeight/2f),
            ),
            gameCamera.position.z
        )
        gameCamera.update()
    }

    override fun handle(event: Event?): Boolean {
        when(event){
            is MapChangeEvent ->{
                maxWidth = event.map.width.toFloat()
                maxHeight = event.map.height.toFloat()
                return true
            }
        }
        return false
    }
}
