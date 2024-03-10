package com.fatih.rpggame.ecs.component

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import com.github.quillraven.fleks.World
import ktx.actors.plusAssign
import ktx.math.plus

class FloatingTextComponent {
    lateinit var damageLabel : Label
    var position : Vector2 = Vector2(0f,0f)
    var endPosition : Vector2 = Vector2(0f,0f)
    var remove : Boolean = false
    companion object{
        class FloatingTextComponentListener(
           @Qualifier("uiStage") private val uiStage: Stage
        ) : ComponentListener<FloatingTextComponent>{
            override fun onComponentAdded(entity: Entity, component: FloatingTextComponent) {
                uiStage.addActor(component.damageLabel)
                component.endPosition.set(component.position.x + (-5..5).random() , component.position.y + (2..5).random())
                component.damageLabel += Actions.sequence(
                    fadeOut(2f, Interpolation.pow3OutInverse),
                    Actions.run {
                        component.remove = true
                    }
                )
            }

            override fun onComponentRemoved(entity: Entity, component: FloatingTextComponent) {
                uiStage.root.removeActor(component.damageLabel)
            }
        }
    }
}
