package com.fatih.rpggame.ecs.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.fatih.rpggame.utils.AnimationType
import com.fatih.rpggame.utils.EntityModel

class AnimationComponent(
    var frameDuration : Float = DEFAULT_FRAME_DURATION,
    var animationTimer : Float = 0f,
    var nextAnimation : String = EMPTY_ANIMATION,
    var playMode: PlayMode = PlayMode.LOOP
) {
    lateinit var animation: Animation<TextureRegionDrawable>
    lateinit var entityModel: EntityModel
    lateinit var animationType: AnimationType

    val isAnimationDone : Boolean
        get() = animation.isAnimationFinished(animationTimer)

    fun nextAnimation(animationType: AnimationType,frameDuration: Float = this.frameDuration,playMode: PlayMode = this.playMode){
        this.animationType = animationType
        this.playMode = playMode
        this.frameDuration = frameDuration
        this.nextAnimation = "${entityModel.entityName}/${animationType.animationName}"
    }

    companion object{
        const val DEFAULT_FRAME_DURATION = 0.1f
        const val EMPTY_ANIMATION = ""
    }
}
