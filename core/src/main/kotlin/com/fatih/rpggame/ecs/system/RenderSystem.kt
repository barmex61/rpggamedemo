package com.fatih.rpggame.ecs.system

import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.RpgGame.Companion.UNIT_SCALE
import com.fatih.rpggame.event.MapChangeEvent
import com.fatih.rpggame.utils.MapLayerType
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.Qualifier
import ktx.graphics.use
import ktx.tiled.forEachLayer

class RenderSystem(
    private val gameStage : Stage,
    @Qualifier("uiStage") private val uiStage : Stage,
    private val rayHandler: RayHandler
) : IntervalSystem() , EventListener{

    private val backgroundLayers = mutableListOf<TiledMapTileLayer>()
    private val foregroundLayers = mutableListOf<TiledMapTileLayer>()
    private val gameCamera = gameStage.camera as OrthographicCamera
    private var map : TiledMap? = null
    private val mapRenderer = OrthogonalTiledMapRenderer(map, UNIT_SCALE,gameStage.batch)

    override fun onTick() {
        gameStage.act(deltaTime)
        gameStage.actors.sort{actor1,actor2 ->
            actor2.y.compareTo(actor1.y)
        }
        mapRenderer.setView(gameCamera)
        gameStage.batch.color = Color.WHITE
        gameStage.batch.use {
            backgroundLayers.forEach {
                mapRenderer.renderTileLayer(it)
            }
        }
        gameStage.draw()
        gameStage.batch.use {
            foregroundLayers.forEach {
                mapRenderer.renderTileLayer(it)
            }
        }
        gameStage.batch.color = Color.WHITE
        rayHandler.setCombinedMatrix(gameCamera)
        rayHandler.updateAndRender()
        uiStage.act(deltaTime)
        uiStage.draw()
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent ->{
                map = event.map
                backgroundLayers.clear()
                foregroundLayers.clear()
                event.map.forEachLayer<TiledMapTileLayer> {
                    when(it.name){
                        MapLayerType.BACKGROUND.layerName -> backgroundLayers.add(it)
                        MapLayerType.FOREGROUND.layerName -> foregroundLayers.add(it)
                        MapLayerType.GROUND.layerName -> backgroundLayers.add(it)
                    }
                }
                return true
            }
        }
        return false
    }

}
