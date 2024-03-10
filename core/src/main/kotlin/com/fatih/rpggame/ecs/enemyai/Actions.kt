package com.fatih.rpggame.ecs.enemyai

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.FloatDistribution
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Vector2
import com.fatih.rpggame.utils.AnimationType
import ktx.math.plus

abstract class Action : LeafTask<SlimeEntity>(){
    val entity : SlimeEntity
        get() = `object`
    override fun copyTo(task: Task<SlimeEntity>): Task<SlimeEntity> = task
}

class IdleTask(
    @JvmField
    @TaskAttribute(name = "duration")
    val duration : FloatDistribution? = null
) : Action() {

    private var taskDuration = duration?.nextFloat()?:3f

    override fun execute(): Status {
        if (status != Status.RUNNING){
            taskDuration = duration?.nextFloat()?:3f
            entity.animation(AnimationType.IDLE)
            return Status.RUNNING
        }
        taskDuration -= GdxAI.getTimepiece().deltaTime
        if (taskDuration <= 0f){
            return Status.SUCCEEDED
        }
        return Status.RUNNING
    }

    override fun copyTo(task: Task<SlimeEntity>): Task<SlimeEntity> {
        (task as WanderTask).duration = duration
        return task
    }
}

class WanderTask(
    @JvmField
    @TaskAttribute(name = "duration")
    var duration : FloatDistribution? = null
) : Action() {

    private var taskDuration = duration?.nextFloat()?:4f
    private val position : Vector2 = Vector2(0f,0f)
    private val targetPosition : Vector2 = Vector2(0f,0f)

    override fun execute(): Status {
        if (status != Status.RUNNING){
            taskDuration = duration?.nextFloat()?:4f
            entity.animation(AnimationType.RUN)
            if (position.isZero){
                position.set(entity.currentPosition)
            }
            targetPosition.set(position.x + (-3..3).random(),position.y + (-3..3).random())
            entity.moveTo(position,targetPosition)
            return Status.RUNNING
        }
        taskDuration -= GdxAI.getTimepiece().deltaTime
        if (taskDuration <= 0f){
            entity.stopMovement()
            return Status.SUCCEEDED
        }
        if(entity.inRange(0.5f,targetPosition)){
            entity.stopMovement()
            return Status.SUCCEEDED
        }
        return Status.RUNNING
    }

    override fun copyTo(task: Task<SlimeEntity>): Task<SlimeEntity> {
        (task as WanderTask).duration = duration
        return task
    }

}
class AttackTask : Action() {

    override fun execute(): Status {
        if (status != Status.RUNNING){
            entity.animation(AnimationType.ATTACK, playMode = Animation.PlayMode.NORMAL)
            entity.startAttack()
            return Status.RUNNING
        }
        if (entity.isAnimationDone){
            entity.animation(AnimationType.IDLE)
            entity.stopMovement()
            return Status.SUCCEEDED
        }
        return Status.RUNNING
    }

}
