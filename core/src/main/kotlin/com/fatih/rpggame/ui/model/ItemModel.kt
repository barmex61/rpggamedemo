package com.fatih.rpggame.ui.model

import com.fatih.rpggame.utils.ItemCategory

data class ItemModel(
    val itemCategory: ItemCategory,
    var equipped : Boolean = false,
    var slotIx : Int = -1,
    var itemEntityId : Int = -1
)
