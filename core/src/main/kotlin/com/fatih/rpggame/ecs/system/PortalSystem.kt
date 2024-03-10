package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.RpgGame.Companion.UNIT_SCALE
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.createBox2dBody
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.createBox2dBodyWithShape2D
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.ecs.component.PortalComponent
import com.fatih.rpggame.ecs.component.SpawnConfig
import com.fatih.rpggame.event.MapChangeEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.utils.Constants
import com.fatih.rpggame.utils.EntityModel
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.assets.disposeSafely
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import ktx.tiled.id
import ktx.tiled.layer
import ktx.tiled.property
import ktx.tiled.shape
import ktx.tiled.x
import ktx.tiled.y
import kotlin.experimental.or

@AllOf([PortalComponent::class])
class PortalSystem(
    private val box2dWorld : World,
    private val portalComps : ComponentMapper<PortalComponent>,
    private val physicComps : ComponentMapper<PhysicComponent>,
    private val imageComps : ComponentMapper<ImageComponent>,
    private val gameStage: Stage
) : IteratingSystem() , EventListener {

    private var map : TiledMap? = null

    fun setMap(path:String,targetPortal : Int = -1){
        map?.disposeSafely()
        world.family(noneOf = arrayOf(PlayerComponent::class)).forEach {
            world.remove(it)
        }
        map = TmxMapLoader().load(path)
        gameStage.fireEvent(MapChangeEvent(map!!))
        if (targetPortal != -1){
            world.family(allOf = arrayOf(PlayerComponent::class)).forEach {
               val targetPort = targetPortalById(map!!,targetPortal)
                configureEntity(it){ player->
                    val width = imageComps[player].image.width
                    val height = imageComps[player].image.height
                    physicComps.remove(player)
                    physicComps.add(player){
                        body = createBox2dBody(box2dWorld, playerConfig.bodyType,width,height,
                            targetPort.x * UNIT_SCALE,targetPort.y * UNIT_SCALE,
                            playerConfig.physicScaling, playerConfig.physicOffset, playerConfig.categoryBits,
                            playerConfig.maskBits)
                    }
                }
            }
        }
    }

    private fun targetPortalById(map:TiledMap,targetPortal: Int) : MapObject {
        return map.layer("portals").objects.first { it.id == targetPortal }
    }

    override fun onTickEntity(entity: Entity) {
        val portalComponent = portalComps[entity]
        portalComponent.run {
            if (triggerEntities.isNotEmpty()){
                triggerEntities.clear()
                setMap(toMap,toPortal)
            }
        }
    }

    override fun handle(event: Event?): Boolean {
        if (event is MapChangeEvent){
            val portalLayer = event.map.layer("portals")
            portalLayer.objects.forEach {mapObject->
                val toMap = mapObject.property<String>("toMap","")
                val toPortal = mapObject.property<Int>("toPortal",-1)
                if (toMap.isBlank()){
                    return@forEach
                }
                world.entity {
                    add<PortalComponent>{
                        id = mapObject.id
                        this.toPortal = toPortal
                        this.toMap = toMap
                    }
                    add<PhysicComponent>{
                       createBox2dBodyWithShape2D(box2dWorld,BodyDef.BodyType.StaticBody,0f,0f,mapObject.shape,true)
                    }
                }
            }
        }
        return false
    }

    override fun onDispose() {
        map.disposeSafely()
    }

    companion object{
        private val playerConfig = SpawnConfig(
            entityModel = EntityModel.PLAYER,
            categoryBits = Constants.PLAYER,
            maskBits =  Constants.OBJECT or Constants.ITEM or Constants.SLIME,
            bodyType = BodyDef.BodyType.DynamicBody,
            physicScaling = vec2(0.22f,0.29f),
            physicOffset = vec2(0f,-0.66f),
            speedScaling = 5f,
            attackRange = 1f,
            attackDamage =  (4..8),
            criticalChance = 50,
            attackScaling = 1.5f,
            lifeScaling = 2f,
            regenerationSpeed = 2f,
            resurrectionTime = 5f,
            hasLight = true
        )
    }
}
