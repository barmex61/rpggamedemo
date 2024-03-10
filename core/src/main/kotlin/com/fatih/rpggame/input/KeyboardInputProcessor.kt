package com.fatih.rpggame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.AttackComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.event.GamePauseEvent
import com.fatih.rpggame.event.GameResumeEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.ui.view.InventoryView
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter

fun addProcessor(inputProcessor: InputProcessor){
    if (Gdx.input.inputProcessor != null){
        (Gdx.input.inputProcessor as InputMultiplexer).addProcessor(inputProcessor)
    }else{
        Gdx.input.inputProcessor = InputMultiplexer(inputProcessor)
    }
}

class KeyboardInputProcessor(
    private val world : World,
    private val moveComps : ComponentMapper<MoveComponent> = world.mapper(),
    private val attackComps : ComponentMapper<AttackComponent> = world.mapper(),
    private val uiStage : Stage,
    private val gameStage: Stage
) : KtxInputAdapter{

    private var moveComponent : MoveComponent? = null
    private var attackComponent : AttackComponent? = null

    init {
        addProcessor(this)
    }

    private var playerSin = 0f
    private var playerCos = 0f

    private var paused : Boolean = false

    private fun Int.isMovementKey() = this == W || this == S || this == A || this == D
    private fun Int.isAttackKey() = this == SPACE
    private fun Int.isInventoryKey() = this == I
    private fun Int.isPauseKey() = this == P

    override fun keyDown(keycode: Int): Boolean {
        if (!keycode.isMovementKey() && !keycode.isAttackKey() && !keycode.isInventoryKey() && !keycode.isPauseKey() ) return false
        when(keycode){
            SPACE -> updateAttack()
            W -> playerSin = 1f
            S -> playerSin = -1f
            A -> playerCos = -1f
            D -> playerCos = 1f
            I -> {
                val visibility = uiStage.actors.filterIsInstance(InventoryView::class.java).first().isVisible
                uiStage.actors.filterIsInstance(InventoryView::class.java).first().isVisible = !visibility
            }
            P-> {
                paused = !paused
                gameStage.fireEvent(if (paused) GamePauseEvent() else GameResumeEvent())
            }
        }
        updateMovement()
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (!keycode.isMovementKey() ) return false
        when(keycode){
            W -> playerSin = if (Gdx.input.isKeyPressed(S)) -1f else 0f
            S -> playerSin = if (Gdx.input.isKeyPressed(W)) 1f else 0f
            A -> playerCos = if (Gdx.input.isKeyPressed(D)) 1f else 0f
            D -> playerCos = if (Gdx.input.isKeyPressed(A)) -1f else 0f
        }
        updateMovement()
        return true
    }

    private fun updateAttack(){
        if (attackComponent == null){
            world.family(allOf = arrayOf(PlayerComponent::class)).forEach {
                attackComponent = attackComps[it]
            }
        }
        attackComponent!!.doAttack = true
    }

    private fun updateMovement(){
        if (moveComponent == null){
            world.family(allOf = arrayOf(PlayerComponent::class)).forEach {
                moveComponent = moveComps[it]
            }
        }
        moveComponent!!.cos = playerCos
        moveComponent!!.sin = playerSin

    }
}
