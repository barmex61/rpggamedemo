package com.fatih.rpggame.ecs.component

import com.fatih.rpggame.utils.ItemCategory
import com.github.quillraven.fleks.Entity

class InventoryComponent {
    val items = mutableListOf<Entity>()
    val itemsToAdd = mutableListOf<ItemCategory>()
    companion object{
        const val CAPACITY = 18
    }
}
