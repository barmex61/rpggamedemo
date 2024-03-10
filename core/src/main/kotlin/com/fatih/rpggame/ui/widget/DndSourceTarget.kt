package com.fatih.rpggame.ui.widget

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target
import com.badlogic.gdx.utils.Scaling
import com.fatih.rpggame.ui.model.ItemModel
import com.fatih.rpggame.utils.ItemCategory

class DndSource (val inventorySlot: InventorySlot) : Source(inventorySlot) {

    val isGear : Boolean
        get() = inventorySlot.supportedCategory != ItemCategory.UNDEFINED

    override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): Payload? {
        if (inventorySlot.itemModel == null) return null
        return Payload().apply {
            `object` = inventorySlot.itemModel
            dragActor = Image(inventorySlot.itemImage.drawable).apply {
                setSize(ACTOR_SIZE, ACTOR_SIZE)
            }
            inventorySlot.item(null)
        }
    }

    override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: Payload, target: Target?) {
        if (target == null) inventorySlot.item(payload.`object` as ItemModel)
    }

    companion object{
        const val ACTOR_SIZE = 22f
    }
}

class DndTarget(
    private val inventorySlot: InventorySlot,
    private val onDrop : (sourceSlot : InventorySlot,targetSlot : InventorySlot ,itemModel : ItemModel) -> Unit
) : Target(inventorySlot){

    private val supportedCategory = inventorySlot.supportedCategory
    private val isGear : Boolean
        get() = supportedCategory != ItemCategory.UNDEFINED
    private fun isSupported(itemCategory: ItemCategory) = supportedCategory == itemCategory

    override fun drag(source: Source, payload: Payload, x: Float, y: Float, pointer: Int): Boolean {
        val dndSource = (source as DndSource)
        val sourceCategory = dndSource.inventorySlot.supportedCategory
        val itemModel = (payload.`object` as ItemModel)
        return if (isGear && isSupported(itemModel.itemCategory)){
            true
        }else if (!isGear && dndSource.isGear && (inventorySlot.isEmpty || inventorySlot.itemModel?.itemCategory == sourceCategory)){
            true
        }else if (!isGear && !dndSource.isGear){
            true
        }else{
            payload.dragActor.setColor(1f,0f,0f,1f)
            false
        }
    }

    override fun reset(source: Source?, payload: Payload) {
        payload.dragActor.color = Color.WHITE
    }

    override fun drop(source: Source, payload: Payload, x: Float, y: Float, pointer: Int) {
        onDrop(
            (source as DndSource).inventorySlot,
            inventorySlot,
            (payload.`object` as ItemModel)
        )
    }

}
