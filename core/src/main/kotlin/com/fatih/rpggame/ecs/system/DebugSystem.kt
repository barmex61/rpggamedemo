package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.enemyai.TEMP_RECT
import com.fatih.rpggame.ecs.system.AttackSystem.Companion.ATTACK_RECT
import com.github.quillraven.fleks.IntervalSystem
import ktx.graphics.use

class DebugSystem(
    private val box2dWorld : World,
    gameStage : Stage
) : IntervalSystem(enabled = true){

    private var debugRenderer : Box2DDebugRenderer? = null
    private var shapeRenderer : ShapeRenderer? = null
    private val gameCamera = gameStage.camera as OrthographicCamera

    init {
        if (enabled){
            debugRenderer = Box2DDebugRenderer()
            shapeRenderer = ShapeRenderer().apply {
                setColor(1f,0f,0f,1f)
            }
        }
    }

    override fun onTick() {
        shapeRenderer?.use(ShapeRenderer.ShapeType.Line,gameCamera.combined) {
            it.rect(
                ATTACK_RECT.x,ATTACK_RECT.y, ATTACK_RECT.width, ATTACK_RECT.height
            )
        }
        shapeRenderer?.use(ShapeRenderer.ShapeType.Line,gameCamera.combined) {
            it.rect(
                TEMP_RECT.x,TEMP_RECT.y, TEMP_RECT.width, TEMP_RECT.height
            )
        }
        debugRenderer?.render(box2dWorld,gameCamera.combined)
    }
}
