package com.fatih.rpggame.ecs.system

import box2dLight.Light
import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.fatih.rpggame.RpgGame.Companion.UNIT_SCALE
import com.fatih.rpggame.actor.FlipImage
import com.fatih.rpggame.ecs.component.AiComponent
import com.fatih.rpggame.ecs.component.AnimationComponent
import com.fatih.rpggame.ecs.component.AttackComponent
import com.fatih.rpggame.ecs.component.CollisionComponent
import com.fatih.rpggame.ecs.component.DialogComponent
import com.fatih.rpggame.ecs.component.DialogId
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.InventoryComponent
import com.fatih.rpggame.ecs.component.LifeComponent
import com.fatih.rpggame.ecs.component.LightComponent
import com.fatih.rpggame.ecs.component.LootComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.ecs.component.MoveComponent.Companion.DEFAULT_SPEED
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.createBox2dBody
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.ecs.component.SpawnComponent
import com.fatih.rpggame.ecs.component.SpawnConfig
import com.fatih.rpggame.ecs.component.StateComponent
import com.fatih.rpggame.event.MapChangeEvent
import com.fatih.rpggame.utils.AnimationType
import com.fatih.rpggame.utils.Constants
import com.fatih.rpggame.utils.EntityModel
import com.fatih.rpggame.utils.ItemCategory
import com.fatih.rpggame.utils.MapLayerType
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.layer
import ktx.tiled.property
import ktx.tiled.width
import ktx.tiled.x
import ktx.tiled.y
import kotlin.experimental.or

@AllOf([SpawnComponent::class])
class EntitySpawnSystem (
    private val spawnComps : ComponentMapper<SpawnComponent>,
    private val box2dWorld : World,
    private val rayHandler: RayHandler
): IteratingSystem() , EventListener {

    private val spawnConfigCache = mutableMapOf<EntityModel, SpawnConfig>()
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))

    override fun onTickEntity(entity: Entity) {
        val spawnComponent = spawnComps[entity]
        val spawnConfig = getSpawnConfig(spawnComponent.entityModel).apply{
            size.set(spawnComponent.size)
            position.set(spawnComponent.position)
        }
        spawnConfig.run {
            world.entity {

                add<ImageComponent>{
                    image = FlipImage().apply {
                        setSize(size.x,size.y)
                        setPosition(position.x,position.y)
                    }
                }
                add<AnimationComponent>{
                    entityModel = if (this@run.entityModel == EntityModel.BLOB) EntityModel.SLIME else this@run.entityModel
                    animationType = if (entityModel == EntityModel.PLAYER) AnimationType.IDLE_RIGHT else AnimationType.IDLE
                    nextAnimation(animationType)
                }
                lateinit var box2dBody : Body
                add<PhysicComponent>{
                    body = createBox2dBody(
                        box2dWorld,bodyType,size.x,size.y,position.x,position.y,
                        physicScaling,physicOffset, categoryBits, maskBits
                    )
                    box2dBody = body
                }
                if (bodyType == BodyDef.BodyType.DynamicBody){
                    add<CollisionComponent>()
                }
                if (speedScaling != 0f){
                    add<MoveComponent>{
                        speed = DEFAULT_SPEED * speedScaling
                    }
                }
                if (attackScaling != 0f){
                    add<AttackComponent>{
                        attackRange = this@run.attackRange
                        criticalChance = this@run.criticalChance
                        attackDamage = this@run.attackDamage
                    }
                }
                if (entityModel == EntityModel.PLAYER){
                    add<PlayerComponent>()
                    add<StateComponent>()
                    add<InventoryComponent>{
                        itemsToAdd += ItemCategory.HELMET
                        itemsToAdd += ItemCategory.ARMOR
                        itemsToAdd += ItemCategory.BOOTS
                        itemsToAdd += ItemCategory.WEAPON
                    }
                }
                if(lifeScaling > 0f){
                    add<LifeComponent>{
                        maxLife = LifeComponent.DEFAULT_MAX_LIFE * lifeScaling
                        currentLife = maxLife
                        regeneration = regenerationSpeed
                        resurrectionTime = this@run.resurrectionTime
                    }
                }
                if (lootable){
                    add<LootComponent>()
                }
                if (dialogId != DialogId.NONE){
                    add<DialogComponent>{
                        this.dialogId = spawnConfig.dialogId
                    }
                }
                if (hasLight){
                    add<LightComponent>{
                        distance = 5f..6.5f
                        light = PointLight(rayHandler,64,LightComponent.lightColor,distance.endInclusive,0f,0f).apply {
                            attachToBody(box2dBody)
                        }
                    }
                }
                if (treePath.isNotEmpty()){
                    add<AiComponent>{
                        treePath = this@run.treePath
                    }
                }
            }
        }
        world.remove(entity)
    }

    private fun getSpawnConfig(entityModel : EntityModel) : SpawnConfig {
        return spawnConfigCache.getOrPut(entityModel){
            when(entityModel){
                EntityModel.PLAYER ->{
                    SpawnConfig(
                        entityModel = entityModel,
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
                EntityModel.SLIME ->{
                    SpawnConfig(
                        entityModel = entityModel,
                        categoryBits = Constants.SLIME,
                        maskBits =  Constants.OBJECT or Constants.ITEM or Constants.PLAYER ,
                        bodyType = BodyDef.BodyType.DynamicBody,
                        physicScaling = vec2(0.45f,0.35f),
                        physicOffset = vec2(0f,-0.15f),
                        speedScaling = 1f,
                        attackRange = 0.5f,
                        attackDamage = (2..5),
                        criticalChance = 30,
                        attackScaling = 1f,
                        lifeScaling = 1f,
                        regenerationSpeed = 2f,
                        treePath = "ai/slime.tree",
                        hasLight = true
                    )
                }
                EntityModel.BLOB ->{
                    SpawnConfig(
                        entityModel = entityModel,
                        categoryBits = Constants.SLIME,
                        maskBits =  Constants.OBJECT or Constants.ITEM or Constants.PLAYER ,
                        bodyType = BodyDef.BodyType.StaticBody,
                        physicScaling = vec2(0.45f,0.35f),
                        physicOffset = vec2(0f,-0.15f),
                        speedScaling = 0f,
                        attackRange = 0f,
                        attackDamage = (0..0),
                        criticalChance = 0,
                        attackScaling = 0f,
                        lifeScaling = 0f,
                        regenerationSpeed = 0f,
                        hasLight = true,
                        dialogId = DialogId.BLOB
                    )
                }
                EntityModel.CHEST ->{
                    SpawnConfig(
                        entityModel = entityModel,
                        categoryBits = Constants.ITEM,
                        maskBits = Constants.SLIME or Constants.OBJECT or Constants.PLAYER,
                        bodyType = BodyDef.BodyType.StaticBody,
                        physicScaling = vec2(1f,1f),
                        physicOffset = vec2(0f,0f),
                        speedScaling = 0f,
                        attackScaling = 0f,
                        lifeScaling = 0f,
                        lootable = true
                    )
                }
            }
        }
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent->{
                event.map.layer(MapLayerType.ENTITY.layerName).objects.forEach {mapObject->
                    val name = mapObject.name?:""
                    if (name.isBlank()) gdxError(GDX_ENTITY_NAME_ERROR + name)
                    if (name == EntityModel.PLAYER.entityName && playerEntities.isNotEmpty){
                        return@forEach
                    }
                    world.entity {
                        add<SpawnComponent>{
                            entityModel = EntityModel.valueOf(name.uppercase())
                            position.set(mapObject.x * UNIT_SCALE,mapObject.y * UNIT_SCALE)
                            size.set(mapObject.width * UNIT_SCALE, mapObject.height * UNIT_SCALE)
                        }
                    }

                }
                return true
            }
        }
        return false
    }

    companion object{
        const val GDX_ENTITY_NAME_ERROR = "Name in the entity layer of map not match for any entity :"
    }

}
