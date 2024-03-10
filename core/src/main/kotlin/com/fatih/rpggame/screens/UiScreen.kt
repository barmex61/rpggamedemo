package com.fatih.rpggame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.fatih.rpggame.ecs.component.InventoryComponent
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.event.EntityAddItemEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.input.addProcessor
import com.fatih.rpggame.ui.model.InventoryModel
import com.fatih.rpggame.ui.model.ItemModel
import com.fatih.rpggame.ui.view.InventoryView
import com.fatih.rpggame.ui.view.inventoryView
import com.fatih.rpggame.utils.ItemCategory
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.scene2d.actors

class UiScreen : KtxScreen {

    private val uiStage = Stage(ExtendViewport(320f,180f)).apply {
        isDebugAll = true
    }
    private var inventoryView : InventoryView ?= null
    private val world = world {  }

    init {
        world.entity {
            add<PlayerComponent>()
            add<InventoryComponent>()
        }
    }

   // private val gameModel = GameModel(world,uiStage)

    override fun show() {
        uiStage.actors {
          // gameView()
            inventoryView = inventoryView(inventoryModel = InventoryModel(world,uiStage))
        }
        addProcessor(uiStage)
    }

    override fun render(delta: Float) {
        uiStage.act(delta)
        uiStage.draw()
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)){
            inventoryView?.item(
                ItemModel(ItemCategory.WEAPON,false,(1..17).random())
            )
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)){
            inventoryView?.item(
                ItemModel(ItemCategory.BOOTS,false,(1..17).random())
            )
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)){
            inventoryView?.item(
                ItemModel(ItemCategory.HELMET,false,(1..17).random())
            )
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)){
            inventoryView?.item(
                ItemModel(ItemCategory.ARMOR,false,(1..17).random())
            )
        }
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width,height,true)
    }
}
