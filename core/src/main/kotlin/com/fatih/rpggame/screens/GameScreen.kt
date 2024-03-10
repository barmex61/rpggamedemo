package com.fatih.rpggame.screens

import box2dLight.Light
import box2dLight.RayHandler
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.fatih.rpggame.dialog.dialog
import com.fatih.rpggame.ecs.component.AiComponent.Companion.AiComponentListener
import com.fatih.rpggame.ecs.component.FloatingTextComponent.Companion.FloatingTextComponentListener
import com.fatih.rpggame.ecs.component.ImageComponent.Companion.ImageComponentListener
import com.fatih.rpggame.ecs.component.LightComponent
import com.fatih.rpggame.ecs.component.LightComponent.Companion.LightComponentListener
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.PhysicComponentListener
import com.fatih.rpggame.ecs.component.StateComponent.Companion.StateComponentListener
import com.fatih.rpggame.ecs.system.AiSystem
import com.fatih.rpggame.ecs.system.AnimationSystem
import com.fatih.rpggame.ecs.system.AttackSystem
import com.fatih.rpggame.ecs.system.AudioSystem
import com.fatih.rpggame.ecs.system.CameraSystem
import com.fatih.rpggame.ecs.system.CollisionDespawnSystem
import com.fatih.rpggame.ecs.system.CollisionSpawnSystem
import com.fatih.rpggame.ecs.system.DeadSystem
import com.fatih.rpggame.ecs.system.DebugSystem
import com.fatih.rpggame.ecs.system.DialogSystem
import com.fatih.rpggame.ecs.system.EntitySpawnSystem
import com.fatih.rpggame.ecs.system.FloatingTextSystem
import com.fatih.rpggame.ecs.system.InventorySystem
import com.fatih.rpggame.ecs.system.LifeSystem
import com.fatih.rpggame.ecs.system.LightSystem
import com.fatih.rpggame.ecs.system.LootSystem
import com.fatih.rpggame.ecs.system.MoveSystem
import com.fatih.rpggame.ecs.system.PhysicSystem
import com.fatih.rpggame.ecs.system.PortalSystem
import com.fatih.rpggame.ecs.system.RenderSystem
import com.fatih.rpggame.ecs.system.StateSystem
import com.fatih.rpggame.event.EntityDialogEvent
import com.fatih.rpggame.event.GamePauseEvent
import com.fatih.rpggame.event.GameResumeEvent
import com.fatih.rpggame.event.MapChangeEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.input.KeyboardInputProcessor
import com.fatih.rpggame.input.addProcessor
import com.fatih.rpggame.ui.model.DialogModel
import com.fatih.rpggame.ui.model.GameModel
import com.fatih.rpggame.ui.model.InventoryModel
import com.fatih.rpggame.ui.view.InventoryView
import com.fatih.rpggame.ui.view.PauseView
import com.fatih.rpggame.ui.view.dialogView
import com.fatih.rpggame.ui.view.gameView
import com.fatih.rpggame.ui.view.inventoryView
import com.fatih.rpggame.ui.view.pauseView
import com.fatih.rpggame.utils.Constants
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.scene2d.actors

class GameScreen(spriteBatch: SpriteBatch, val setScreen : (type : Class<KtxScreen>) -> Unit) : KtxScreen ,
    EventListener {

    private val textureAtlas = TextureAtlas("graphics/gameObjects.atlas")
    private val gameViewport = ExtendViewport(16f,9f)
    private val uiViewport = ExtendViewport(320f,180f)
    private val gameStage = Stage(gameViewport,spriteBatch)
    private val uiStage = Stage(uiViewport,spriteBatch)
    private val box2dWorld = createWorld().apply {
        autoClearForces = false
    }
    private var inventoryView : InventoryView?= null
    private val rayHandler = RayHandler(box2dWorld).apply {
        RayHandler.useDiffuseLight(true)
        setAmbientLight(Color.ROYAL)
        Light.setGlobalContactFilter(Constants.LIGHT,0,Constants.OBJECT )
    }
    private val world = world {

        components {
            add<ImageComponentListener>()
            add<PhysicComponentListener>()
            add<StateComponentListener>()
            add<FloatingTextComponentListener>()
            add<AiComponentListener>()
            add<LightComponentListener>()
        }

        injectables {
            add(box2dWorld)
            add(textureAtlas)
            add(gameStage)
            add("uiStage",uiStage)
            add(rayHandler)
        }

        systems {
            add<EntitySpawnSystem>()
            add<CollisionSpawnSystem>()
            add<CollisionDespawnSystem>()
            add<PortalSystem>()
            add<AnimationSystem>()
            add<MoveSystem>()
            add<PhysicSystem>()
            add<AttackSystem>()
            add<LifeSystem>()
            add<DeadSystem>()
            add<LootSystem>()
            add<DialogSystem>()
            add<InventorySystem>()
            add<FloatingTextSystem>()
            add<AudioSystem>()
            add<AiSystem>()
            add<StateSystem>()
            add<LightSystem>()
            add<CameraSystem>()
            add<RenderSystem>()
            add<DebugSystem>()
        }
    }
    private val gameModel = GameModel(uiStage)

    init {
        world.systems.forEach {
            if (it is EventListener) gameStage.addListener(it)
        }
        uiStage.actors {
            gameView(gameModel = gameModel)
            dialogView(DialogModel(gameStage))
            inventoryView = inventoryView(inventoryModel = InventoryModel(world,gameStage)).apply { isVisible = false }
            pauseView().apply { isVisible = false }
        }
        gameStage.addListener(gameModel)
        gameStage.addListener(this)
        world.system<PortalSystem>().setMap("map/map1.tmx")
        addProcessor(KeyboardInputProcessor(world, uiStage = uiStage, gameStage = gameStage))
        addProcessor(uiStage)
    }

    private fun pauseWorld(pause:Boolean){
        uiStage.actors.filterIsInstance<PauseView>().first().isVisible = pause
        val mandatorySystems = setOf(
            CameraSystem::class,
            RenderSystem::class,
            DebugSystem::class
        )
        world.systems.filter { it::class !in mandatorySystems }.forEach { it.enabled = !pause }
    }

    override fun render(delta: Float) {
        world.update(delta.coerceAtMost(0.25f))
        GdxAI.getTimepiece().update(delta.coerceAtMost(0.25f))
    }

    override fun pause() {
        pauseWorld(true)
    }

    override fun resume() {
        pauseWorld(false)
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width,height,true)
        uiStage.viewport.update(width,height,true)
        rayHandler.useCustomViewport(gameStage.viewport.screenX,gameStage.viewport.screenY,
            gameStage.viewport.screenWidth, gameStage.viewport.screenHeight
        )
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is GameResumeEvent -> resume()
            is GamePauseEvent -> pause()
        }
        return false
    }

    override fun dispose() {
        textureAtlas.disposeSafely()
        gameStage.disposeSafely()
        world.dispose()
        box2dWorld.disposeSafely()
        uiStage.disposeSafely()

    }
}
