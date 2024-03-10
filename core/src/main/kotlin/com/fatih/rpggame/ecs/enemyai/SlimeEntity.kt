package com.fatih.rpggame.ecs.enemyai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.fatih.rpggame.ecs.component.AiComponent
import com.fatih.rpggame.ecs.component.AnimationComponent
import com.fatih.rpggame.ecs.component.AnimationComponent.Companion.DEFAULT_FRAME_DURATION
import com.fatih.rpggame.ecs.component.AttackComponent
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.utils.AnimationType
import com.fatih.rpggame.utils.AttackState
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import ktx.math.compareTo
import ktx.math.plus

val TEMP_RECT = Rectangle()

class SlimeEntity(
    world : World,
    val entity : Entity,
    animComps : ComponentMapper<AnimationComponent> = world.mapper(),
    private val physicComps : ComponentMapper<PhysicComponent> = world.mapper(),
    moveComps : ComponentMapper<MoveComponent> = world.mapper(),
    aiComps : ComponentMapper<AiComponent> = world.mapper(),
    attackComps : ComponentMapper<AttackComponent> = world.mapper(),
    imageComps : ComponentMapper<ImageComponent> = world.mapper()
) {

    private val animationComponent = animComps[entity]
    private val physicComponent = physicComps[entity]
    private val moveComponent = moveComps[entity]
    private val aiComponent = aiComps[entity]
    private val attackComponent = attackComps[entity]
    private val imageComponent = imageComps[entity]

    val currentPosition : Vector2
        get() = physicComponent.body.position

    val isAnimationDone : Boolean
        get() = animationComponent.isAnimationDone

    fun isEnemyNearby() : Boolean {
        return aiComponent.nearbyEntities.isNotEmpty()
    }

    fun canAttack() : Boolean {
        if (attackComponent.attackState != AttackState.READY) return false
        if (aiComponent.nearbyEntities.isEmpty()) return false
        val targetEntity = aiComponent.nearbyEntities.first()
        val physicComponent = physicComps[targetEntity]
        val targetPosition = physicComponent.body.position
        val targetOffset = physicComponent.bodyOffset
        return inRange(attackComponent.attackRange * 2f,targetPosition + targetOffset)
    }

    fun startAttack(){
        val targetEntity = aiComponent.nearbyEntities.first()
        val targetPosition = physicComps[targetEntity].body.position
        imageComponent.image.flipX = targetPosition < currentPosition
        attackComponent.doAttack = true
        attackComponent.startAttack()
    }

    fun stopMovement(){
        moveComponent.apply {
            cos = 0f
            sin = 0f
        }
    }

    fun moveTo(startPosition : Vector2 , targetPosition : Vector2){
        val radian = MathUtils.atan2(targetPosition.y - startPosition.y , targetPosition.x - startPosition.x)
        moveComponent.cos = MathUtils.cos(radian)
        moveComponent.sin = MathUtils.sin(radian)
    }

    fun inRange(radius : Float , targetPosition: Vector2) : Boolean{
        val offset = physicComponent.bodyOffset
        val size = physicComponent.bodySize
        val rectPosX = currentPosition.x + offset.x - size.x /2f
        val rectPoxY = currentPosition.y + offset.y - size.y /2f
        TEMP_RECT.set(
            rectPosX - radius,
            rectPoxY - radius,
            radius * 2f + size.x,
            radius * 2f + size.y
        )
        return TEMP_RECT.contains(targetPosition)
    }

    fun animation(animationType: AnimationType,frameDuration : Float = DEFAULT_FRAME_DURATION, playMode: PlayMode = PlayMode.LOOP){
        animationComponent.nextAnimation(animationType,frameDuration, playMode)
    }
}
