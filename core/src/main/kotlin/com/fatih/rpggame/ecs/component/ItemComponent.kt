package com.fatih.rpggame.ecs.component

import com.fatih.rpggame.utils.ItemCategory

data class ItemComponent (
    var itemCategory : ItemCategory= ItemCategory.UNDEFINED,
    var slotIx : Int = -1,
    var equipped : Boolean = false
)

