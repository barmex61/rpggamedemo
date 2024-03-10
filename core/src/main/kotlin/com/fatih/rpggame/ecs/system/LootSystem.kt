package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.AnimationComponent
import com.fatih.rpggame.ecs.component.LootComponent
import com.fatih.rpggame.event.LootEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.utils.AnimationType
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([LootComponent::class])
class LootSystem (
    private val lootComps : ComponentMapper<LootComponent>,
    private val animComps : ComponentMapper<AnimationComponent>,
    private val gameStage : Stage
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val lootComponent = lootComps[entity]
        lootComponent.run {
            if (!isLooted){
                return
            }
            configureEntity(entity){
                lootComps.remove(it)
                gameStage.fireEvent(LootEvent("You found something special !"))
                animComps[it].nextAnimation(AnimationType.OPEN, frameDuration = 0.5f, playMode = Animation.PlayMode.NORMAL)
            }
        }
    }

}
