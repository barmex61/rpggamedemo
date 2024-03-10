package com.fatih.rpggame.ecs.enemyai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task

abstract class Conditions : LeafTask<SlimeEntity>(){
    val entity : SlimeEntity
        get() = `object`
    override fun copyTo(task: Task<SlimeEntity>) = task
    override fun execute(): Status {
        return if (condition()){
            Status.SUCCEEDED
        }else{
            Status.FAILED
        }
    }
    abstract fun condition() : Boolean
}

class IsEnemyNearby : Conditions() {

    override fun condition() = entity.isEnemyNearby()
}

class CanAttack : Conditions(){
    override fun condition() = entity.canAttack()
}
