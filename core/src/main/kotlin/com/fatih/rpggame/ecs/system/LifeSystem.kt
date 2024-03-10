package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.fatih.rpggame.ecs.component.DeadComponent
import com.fatih.rpggame.ecs.component.FloatingTextComponent
import com.fatih.rpggame.ecs.component.LifeComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.event.EnemyDamageEvent
import com.fatih.rpggame.event.EnemyDeathEvent
import com.fatih.rpggame.event.PlayerDamageEvent
import com.fatih.rpggame.event.PlayerDeathEvent
import com.fatih.rpggame.event.fireEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.NoneOf

@AllOf([LifeComponent::class])
@NoneOf([DeadComponent::class])
class LifeSystem (
    private val lifeComps : ComponentMapper<LifeComponent>,
    private val deadComps : ComponentMapper<DeadComponent>,
    private val physicComps : ComponentMapper<PhysicComponent>,
    private val gameStage : Stage,
    private val playerComps : ComponentMapper<PlayerComponent>
) : IteratingSystem(){

    private val damageFont = BitmapFont(Gdx.files.internal("ui/damagefont.fnt")).apply { data.setScale(0.45f) }
    private val damageTextStyle = LabelStyle(damageFont, Color.RED)

    override fun onTickEntity(entity: Entity) {
        val lifeComponent = lifeComps[entity]
        lifeComponent.run {
            if (currentLife < maxLife && regeneration != 0f){
                currentLife = (currentLife + regeneration * deltaTime).coerceAtMost(maxLife)
            }
            if (damageTaken != 0f ){
                currentLife -= damageTaken
                createFloatingTextEntity(isCrit,damageTaken.toInt(),physicComps[entity].body.position)
                if (entity in playerComps){
                    gameStage.fireEvent(PlayerDamageEvent(currentLife / maxLife))
                }else{
                    gameStage.fireEvent(EnemyDamageEvent(currentLife / maxLife))
                }
                damageTaken = 0f
            }
            if (currentLife <= 0f && entity !in deadComps){
                if (entity in playerComps) gameStage.fireEvent(PlayerDeathEvent()) else gameStage.fireEvent(EnemyDeathEvent())
                configureEntity(entity){
                    deadComps.add(it){
                        resurrectionTime = lifeComponent.resurrectionTime
                        canResurrect = lifeComponent.resurrectionTime > 0f
                    }
                }
            }
        }
    }

    private fun createFloatingTextEntity(isCrit : Boolean , damage : Int , position : Vector2){
        world.entity {
            add<FloatingTextComponent>{
                this.position.set(position)
                damageLabel = Label(damage.toString() + if (isCrit) " Crit!" else "", damageTextStyle)
            }
        }
    }
}
