package com.fatih.rpggame.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.InventoryComponent
import com.fatih.rpggame.ecs.component.ItemComponent
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.event.EntityAddItemEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.utils.ItemCategory
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

class InventoryModel (
    world: World,
    val gameStage : Stage
) : EventListener{

    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))
    private val inventoryComps : ComponentMapper<InventoryComponent> = world.mapper()
    private val itemComps : ComponentMapper<ItemComponent> = world.mapper()
    private val playerComps : ComponentMapper<PlayerComponent> = world.mapper()
    private val playerInventoryComps : InventoryComponent
        get() = inventoryComps[playerEntities.first()]
    private var playerItemList = mutableListOf<ItemModel>()
    var playerItemListCallback : ((List<ItemModel>) -> Unit)? = null

    init {
        gameStage.addListener(this)
    }

    fun equip(itemModel: ItemModel,equip : Boolean){
        playerItemByModel(itemModel).equipped = equip
        itemModel.equipped = equip
    }

    private fun playerItemByModel(itemModel: ItemModel) : ItemComponent {
        return itemComps[playerInventoryComps.items.first{it.id == itemModel.itemEntityId}]
    }

    fun inventoryItem(slotIx : Int,itemModel :ItemModel){
        playerItemByModel(itemModel).slotIx = slotIx
        itemModel.slotIx = slotIx
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is EntityAddItemEvent ->{
                if (event.entity in playerComps){

                    playerItemList.addAll(inventoryComps[event.entity].items.map {
                        val itemComponent = itemComps[it]
                        ItemModel(
                            itemComponent.itemCategory,itemComponent.equipped,itemComponent.slotIx,it.id
                        )
                    })

                    playerItemListCallback?.invoke(playerItemList)
                }
                return true
            }
        }
        return false
    }
}
