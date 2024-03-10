package com.fatih.rpggame.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.fatih.rpggame.ui.Drawables
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import com.fatih.rpggame.ui.get
import com.fatih.rpggame.ui.model.ItemModel
import com.fatih.rpggame.utils.ItemCategory
import ktx.actors.alpha
import ktx.actors.plusAssign

class InventorySlot(
    val skin: Skin,
    val drawables: Drawables? = null
) : KGroup , WidgetGroup() {

    private val background = Image(skin[Drawables.INVENTORY_SLOT])
    private val itemBgd: Image? = if (drawables == null) null else Image(skin[drawables])
    val itemImage = Image()
    var itemModel : ItemModel? = null
    val isGear : Boolean
        get() = supportedCategory != ItemCategory.UNDEFINED

    val supportedCategory : ItemCategory
        get() = when(drawables){
            Drawables.INVENTORY_SLOT_ARMOR -> ItemCategory.ARMOR
            Drawables.INVENTORY_SLOT_HELMET -> ItemCategory.HELMET
            Drawables.INVENTORY_SLOT_WEAPON -> ItemCategory.WEAPON
            Drawables.INVENTORY_SLOT_BOOTS -> ItemCategory.BOOTS
            else -> ItemCategory.UNDEFINED
        }

    val isEmpty : Boolean
        get() = itemModel == null

    init {
        this += background
        itemBgd?.let {
            this += it.apply {
                alpha = 0.33f
                setPosition(3f,3f)
                setSize(14f,14f)
                setScaling(Scaling.fill)
            }
        }
        this += itemImage.apply {
            setPosition(3f,3f)
            setSize(14f,14f)
            setScaling(Scaling.fill)
        }
    }

    fun item(itemModel: ItemModel?){
        this.itemModel = itemModel
        if (itemModel == null){
            itemImage.drawable = null
        }else{
            itemImage.drawable = skin[itemModel.itemCategory.drawables]
        }
    }

    override fun getPrefWidth() = background.drawable.minWidth

    override fun getPrefHeight() = background.drawable.minHeight
}

fun <S>KWidget<S>.inventorySlot(
    drawables: Drawables? = null,
    skin : Skin = Scene2DSkin.defaultSkin,
    init : InventorySlot.(S) -> Unit = {}
) : InventorySlot = actor(InventorySlot(skin,drawables),init)
