package com.fatih.rpggame.ecs.component

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.fatih.rpggame.utils.EntityModel
import ktx.math.vec2

data class SpawnConfig(
    val size : Vector2 = vec2(),
    val position: Vector2 = vec2(),
    val entityModel: EntityModel,
    val categoryBits : Short,
    val maskBits : Short,
    val bodyType: BodyType,
    val physicScaling : Vector2,
    val physicOffset : Vector2,
    val speedScaling : Float,
    val attackRange : Float = 0f,
    val attackDamage : IntRange = (0..0),
    val criticalChance : Int = 0,
    val attackScaling : Float = 0f,
    val lifeScaling : Float ,
    val regenerationSpeed : Float = 1f,
    val resurrectionTime : Float = 0f,
    val lootable : Boolean = false,
    val treePath : String = "",
    val hasLight : Boolean = false,
    val dialogId : DialogId = DialogId.NONE

)

data class SpawnComponent(
    val size : Vector2 = vec2(),
    var position : Vector2 = vec2(),
    val spawnColor : Color = Color.WHITE
){
    lateinit var entityModel: EntityModel
}

