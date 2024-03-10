package com.fatih.rpggame.ecs.component

import com.badlogic.gdx.math.Polyline
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.fatih.rpggame.RpgGame.Companion.UNIT_SCALE
import com.fatih.rpggame.ecs.system.CollisionSpawnSystem.Companion.SPAWN_AREA_SIZE
import com.fatih.rpggame.utils.Constants
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import ktx.app.gdxError
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2
import kotlin.experimental.or

class PhysicComponent {

    lateinit var body : Body
    val impulse = Vector2(0f,0f)
    val previousPos = Vector2(0f,0f)
    val bodySize : Vector2 = Vector2(0f,0f)
    val bodyOffset : Vector2 = Vector2(0f,0f)

    companion object{

        const val HIT_BOX = "Hit box"
        const val PORTAL_OBJECT = "Portal object"
        const val COLLISION_BOX ="Collision box"
        const val COLLISION_CIRCLE_OBJECT = "Collision object"
        const val AI_COLLISION_CIRCLE = "Ai collision"
        private const val SHAPE_NOT_SUPPORTED = "Shape is not supported"

        fun PhysicComponent.createBox2dBody(
            world: World,
            bodyType: BodyType,
            width : Float,
            height : Float,
            posX : Float,
            posY : Float,
            physicScaling : Vector2,
            physicOffset : Vector2,
            categoryBits : Short,
            maskBits : Short,

        ) : Body {
            return world.body(bodyType){
                fixedRotation = true
                allowSleep = false
                position.set(posX + width/2f,posY + height/2f)
                val w = width * physicScaling.x
                val h = height * physicScaling.y
                bodySize.set(w,h)
                bodyOffset.set(physicOffset)
                val offset = vec2(physicOffset.x,physicOffset.y - h /3f)
                box(w,h ,physicOffset){
                    this.isSensor = bodyType == BodyType.DynamicBody
                    this.filter.categoryBits = categoryBits
                    this.filter.maskBits = maskBits
                    this.userData = COLLISION_BOX
                }
                if (bodyType == BodyType.DynamicBody){
                     box(w,h / 3f,offset){
                        this.isSensor = false
                        this.userData = HIT_BOX
                        this.filter.categoryBits = categoryBits
                        this.filter.maskBits = maskBits
                     }
                }
                if (categoryBits == Constants.SLIME){
                    circle(SPAWN_AREA_SIZE.toFloat()){
                        isSensor = true
                        userData = AI_COLLISION_CIRCLE
                        filter.categoryBits = categoryBits
                        filter.maskBits = maskBits
                    }
                }
            }
        }

        fun PhysicComponent.createBox2dBodyWithShape2D(
            world: World,
            bodyType: BodyType,
            x : Float,
            y : Float,
            shape: Shape2D,
            isPortal : Boolean = false
        ) : Body {
           body =  world.body(bodyType){
                fixedRotation = true
                allowSleep = false
                when(shape){
                    is Rectangle ->{
                        val width = shape.width * UNIT_SCALE
                        val height = shape.height * UNIT_SCALE
                        val posX = x + shape.x * UNIT_SCALE
                        val posY = y + shape.y * UNIT_SCALE
                        position.set(posX,posY)
                        loop(floatArrayOf(
                            0f,0f,width,0f,width,height,0f,height
                        )){
                            isSensor = isPortal
                            if (isPortal) userData = PORTAL_OBJECT
                            filter.categoryBits = Constants.OBJECT
                            filter.maskBits = Constants.SLIME or Constants.PLAYER or Constants.LIGHT
                        }
                        if (!isPortal){
                            circle(SPAWN_AREA_SIZE * 1.6f){
                                isSensor = true
                                userData = COLLISION_CIRCLE_OBJECT
                                filter.categoryBits = Constants.OBJECT
                                filter.maskBits = Constants.PLAYER or Constants.SLIME
                            }
                        }
                    }
                    is Polyline -> {
                        position.set(0f,0f)
                        loop(shape.vertices){
                            filter.categoryBits = Constants.OBJECT
                            filter.maskBits = Constants.SLIME or Constants.PLAYER
                        }
                    }
                    else -> gdxError(SHAPE_NOT_SUPPORTED + shape)
                }
            }
            return body
        }

        class PhysicComponentListener(private val world: World) : ComponentListener<PhysicComponent>{
            override fun onComponentAdded(entity: Entity, component: PhysicComponent) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicComponent) {
                world.destroyBody(component.body)
                component.body.userData = null
            }
        }
    }
}
