package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.InventoryComponent
import com.fatih.rpggame.ecs.component.InventoryComponent.Companion.CAPACITY
import com.fatih.rpggame.ecs.component.ItemComponent
import com.fatih.rpggame.event.EntityAddItemEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.utils.ItemCategory
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([InventoryComponent::class])
class InventorySystem(
    private val inventoryComps : ComponentMapper<InventoryComponent>,
    private val itemComps : ComponentMapper<ItemComponent>,
    private val gameStage: Stage
) : IteratingSystem(){

    override fun onTickEntity(entity: Entity) {
        val inventoryComponent = inventoryComps[entity]
        if (inventoryComponent.itemsToAdd.isEmpty()) return
        inventoryComponent.itemsToAdd.forEach {
            val slotIx = emptySlotIx(inventoryComponent)
            if (slotIx == -1) return
            val newItem = spawnItem(it,slotIx)
            inventoryComponent.items += newItem
            gameStage.fireEvent(EntityAddItemEvent(entity,newItem))
        }
        inventoryComponent.itemsToAdd.clear()

    }

    private fun spawnItem(itemCategory: ItemCategory,slotIx : Int) : Entity {
        return world.entity {
            add<ItemComponent>{
                this.itemCategory = itemCategory
                this.slotIx = slotIx
                this.equipped = false
            }
        }
    }

    private fun emptySlotIx(inventoryComponent: InventoryComponent) : Int {
        (0..<CAPACITY).forEach {i->
            if (inventoryComponent.items.none { itemComps[it].slotIx == i}){
                return i
            }
        }
        return -1
    }
}
