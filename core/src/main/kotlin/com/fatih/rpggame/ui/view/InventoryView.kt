package com.fatih.rpggame.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.fatih.rpggame.ui.Drawables
import com.fatih.rpggame.ui.Labels
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table
import com.fatih.rpggame.ui.get
import com.fatih.rpggame.ui.model.InventoryModel
import com.fatih.rpggame.ui.model.ItemModel
import com.fatih.rpggame.ui.widget.DndSource
import com.fatih.rpggame.ui.widget.DndSource.Companion.ACTOR_SIZE
import com.fatih.rpggame.ui.widget.DndTarget
import com.fatih.rpggame.ui.widget.InventorySlot
import com.fatih.rpggame.ui.widget.inventorySlot
import ktx.scene2d.Scene2dDsl

class InventoryView (
    skin: Skin,
    val inventoryModel: InventoryModel
): KTable , Table() {

    private val gearSlots = mutableListOf<InventorySlot>()
    private val inventorySlots = mutableListOf<InventorySlot>()

    init {
        setFillParent(true)
        val titlePadding = 15f
        table {inventoryTableCell->
            background = skin[Drawables.FRAME_BGD]
            label(text = "Inventory", style = Labels.TITLE.labelName,skin){
                this.setAlignment(Align.center)
                it.pad(8f,titlePadding,0f,titlePadding).row()
            }

            table {inventorySlotTableCell->
                inventorySlotTableCell.expand()
                for (i in 1..18){
                    this@InventoryView.inventorySlots += inventorySlot(skin = skin){slotCell->
                        slotCell.padBottom(2f)
                        if (i%6 == 0){
                            slotCell.row()
                        }else{
                            slotCell.padRight(2f)
                        }
                    }
                }
            }
            inventoryTableCell.width(150f).height(120f).padRight(50f)
        }
        table {gearTableCell->
            background = skin[Drawables.FRAME_BGD]
            label(text = "Gear", style = Labels.TITLE.labelName,skin){
                this.setAlignment(Align.center)
                it.pad(8f,titlePadding,0f,titlePadding).row()
            }
            table {gearSlotTableCell->
                gearSlotTableCell.expand()
                this@InventoryView.gearSlots += inventorySlot(Drawables.INVENTORY_SLOT_HELMET,skin){
                    it.colspan(2).row()
                }
                this@InventoryView.gearSlots += inventorySlot(Drawables.INVENTORY_SLOT_WEAPON,skin){
                    it.padBottom(2f).padRight(2f)
                }
                this@InventoryView.gearSlots += inventorySlot(Drawables.INVENTORY_SLOT_ARMOR,skin){
                    it.padBottom(2f).row()
                }
                this@InventoryView.gearSlots += inventorySlot(Drawables.INVENTORY_SLOT_BOOTS,skin){
                    it.colspan(2)
                }

            }
            gearTableCell.width(90f).height(120f)
        }
        setupDragAndDrop()
        inventoryModel.playerItemListCallback = {itemModels ->
            clearInventoryAndGear()
            itemModels.forEach {
                if(it.equipped){
                    gear(it)
                }else{
                    item(it)
                }
            }
        }
    }
    private fun clearInventoryAndGear(){
        inventorySlots.forEach { it.item(null) }
        gearSlots.forEach { it.item(null) }
    }

    private fun setupDragAndDrop(){
        val dnd = DragAndDrop()
        dnd.setDragActorPosition(ACTOR_SIZE/2f,-ACTOR_SIZE/2F)
        inventorySlots.forEach {
            dnd.addSource(DndSource(it))
            dnd.addTarget(DndTarget(it,::onDropItem))
        }
        gearSlots.forEach {
            dnd.addSource(DndSource(it))
            dnd.addTarget(DndTarget(it,::onDropItem))
        }
    }

    private fun onDropItem(sourceSlot : InventorySlot,targetSlot : InventorySlot,itemModel: ItemModel){
        if (sourceSlot == targetSlot) return
        sourceSlot.item(targetSlot.itemModel)
        targetSlot.item(itemModel)

        val sourceItemModel = sourceSlot.itemModel
        if (sourceSlot.isGear){
            inventoryModel.equip(itemModel,false)
            if (sourceItemModel != null){
                inventoryModel.equip(sourceItemModel,true)
            }
        }else if (sourceItemModel != null){
            inventoryModel.inventoryItem(inventorySlots.indexOf(sourceSlot),sourceItemModel)
        }
        if (targetSlot.isGear){
            if (sourceItemModel != null){
                inventoryModel.equip(sourceItemModel,false)
            }
            inventoryModel.equip(itemModel,true)
        }else {
            inventoryModel.inventoryItem(inventorySlots.indexOf(targetSlot),itemModel)
        }
    }

    fun item(itemModel: ItemModel){
        inventorySlots[itemModel.slotIx].item(itemModel)
    }

    fun gear(itemModel: ItemModel){
        gearSlots.firstOrNull { it.supportedCategory == itemModel.itemCategory }?.item(itemModel)
    }


}

@Scene2dDsl
fun <S>KWidget<S>.inventoryView(
    skin: Skin = Scene2DSkin.defaultSkin,
    inventoryModel: InventoryModel,
    init : InventoryView.(S) -> Unit = {}
) : InventoryView = actor(InventoryView(skin,inventoryModel),init)
