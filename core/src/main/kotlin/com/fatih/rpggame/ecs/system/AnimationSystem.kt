package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.fatih.rpggame.ecs.component.AnimationComponent
import com.fatih.rpggame.ecs.component.AnimationComponent.Companion.DEFAULT_FRAME_DURATION
import com.fatih.rpggame.ecs.component.AnimationComponent.Companion.EMPTY_ANIMATION
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.collections.map

@AllOf([AnimationComponent::class])
class AnimationSystem (
    private val animComps : ComponentMapper<AnimationComponent>,
    private val imageComps : ComponentMapper<ImageComponent>,
    private val textureAtlas : TextureAtlas,
    private val moveComps : ComponentMapper<MoveComponent>
): IteratingSystem(){

    private val animationCache = mutableMapOf<String,Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        val animationComponent = animComps[entity]
        val imageComponent = imageComps[entity]
        animationComponent.run {
            animationTimer += deltaTime
            if (nextAnimation != EMPTY_ANIMATION){
                animation = getAnimation(nextAnimation).apply {
                    frameDuration = this@run.frameDuration
                    playMode = this@run.playMode
                }
                animationTimer = 0f
                nextAnimation = EMPTY_ANIMATION
            }
            imageComponent.image.drawable = animation.getKeyFrame(animationTimer)
        }
    }

    private fun getAnimation(nextAnimation : String) : Animation<TextureRegionDrawable> {
        return animationCache.getOrPut(nextAnimation){
            Animation(DEFAULT_FRAME_DURATION,textureAtlas.findRegions(nextAnimation).map {
                TextureRegionDrawable(it)
            })
        }
    }
}
