package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.math.Polyline
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.fatih.rpggame.ecs.component.CollisionComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.createBox2dBodyWithShape2D
import com.fatih.rpggame.ecs.component.TiledComponent
import com.fatih.rpggame.event.CollisionDespawnEvent
import com.fatih.rpggame.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.tiled.forEachLayer
import ktx.tiled.height
import ktx.tiled.isEmpty
import ktx.tiled.shape
import ktx.tiled.width

@AllOf([CollisionComponent::class])
class CollisionSpawnSystem(
    private val physicComps : ComponentMapper<PhysicComponent>,
    private val box2dWorld :World
) : IteratingSystem() , EventListener {

    private val tiledMapTileLayers = mutableListOf<TiledMapTileLayer>()
    private val processedCells = mutableSetOf<Cell>()

    private fun TiledMapTileLayer.eachCell(radius : Int,posX : Int,posY:Int,callBack : (Cell,x:Int,y:Int) -> Unit){
        for (x in posX-radius .. posX+radius){
            for (y in posY-radius .. posY+radius){
                getCell(x,y)?.let { cell ->
                    callBack(cell,x,y)
                }
            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        val physicComponent = physicComps[entity]
        val pos = physicComponent.body.position
        tiledMapTileLayers.forEach { layer->
            layer.eachCell(SPAWN_AREA_SIZE,pos.x.toInt(),pos.y.toInt()){cell,posX,posY->
                if (cell in processedCells) return@eachCell
                if (cell.tile.objects.isEmpty()) return@eachCell
                processedCells.add(cell)
                world.entity {
                    cell.tile.objects.forEach { mapObject->
                        add<TiledComponent>{
                            this.cell = cell
                            this.nearbyEntities += entity
                        }
                        add<PhysicComponent>{
                           body = createBox2dBodyWithShape2D(box2dWorld,BodyDef.BodyType.StaticBody,posX.toFloat(),posY.toFloat(),mapObject.shape)
                        }
                    }

                }
            }
        }
    }

    override fun handle(event: Event?): Boolean {
        when(event){
            is MapChangeEvent ->{
                tiledMapTileLayers.clear()
                event.map.forEachLayer<TiledMapTileLayer> {
                    tiledMapTileLayers.add(it)
                }
                world.entity {
                    val width = event.map.width.toFloat()
                    val height = event.map.height.toFloat()
                    add<PhysicComponent>{
                        body = createBox2dBodyWithShape2D(box2dWorld,BodyDef.BodyType.StaticBody,0f,0f,Polyline(floatArrayOf( 0f,0f,width,0f,width,height,0f,height)))
                    }
                }
                return true
            }
            is CollisionDespawnEvent ->{
                processedCells -= event.cell
            }
        }
        return false
    }

    companion object{
        const val SPAWN_AREA_SIZE = 3
    }
}
