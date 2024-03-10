package com.fatih.rpggame.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ktx.style.button
import ktx.style.label
import ktx.style.set
import ktx.style.skin
import ktx.style.textButton

enum class Fonts(val atlasKey: String,val scaling : Float){
    DEFAULT("fnt_white",0.25f),
    BIG("fnt_white",0.5f);
    val skinKey = "Font_$atlasKey"
    val fontPath = "ui/${atlasKey}.fnt"

}

enum class Labels{
    FRAME,TITLE,LARGE;
    val labelName = name.lowercase()
}

enum class Drawables(val atlasKey : String){
    CHAR_INFO_BGD("char_info"),
    LIFE_BAR("life_bar"),
    PLAYER("player"),
    INVENTORY_SLOT("inv_slot"),
    INVENTORY_SLOT_HELMET("inv_slot_helmet"),
    INVENTORY_SLOT_ARMOR("inv_slot_armor"),
    INVENTORY_SLOT_BOOTS("inv_slot_boots"),
    INVENTORY_SLOT_WEAPON("inv_slot_weapon"),
    SLIME("slime"),
    MANA_BAR("mana_bar"),
    FRAME_BGD("frame_bgd"),
    FRAME_FGD("frame_fgd"),
    ARMOR("armor"),
    BOOTS("boots"),
    HELMET("helmet"),
    WEAPON1("sword"),
    WEAPON2("sword2"),
    UNDEFINED("")
}

enum class Buttons{
    TEXT_BUTTON;
    val skinKey = name.lowercase()
}

operator fun Skin.get(drawables: Drawables): Drawable = this.getDrawable(drawables.atlasKey)
operator fun Skin.get(font : Fonts) : BitmapFont = this.getFont(font.skinKey)

fun loadSkin() {
    Scene2DSkin.defaultSkin = skin(TextureAtlas("ui/ui.atlas")){ skin ->
        Fonts.entries.forEach {
            skin[it.skinKey] = BitmapFont(Gdx.files.internal(it.fontPath),skin.getRegion(it.atlasKey)).apply {
                data.setScale(it.scaling)
                data.markupEnabled = true
            }
        }
        label(name = Labels.FRAME.labelName){
            font = skin[Fonts.DEFAULT]
            background = skin[Drawables.FRAME_FGD].apply {
                leftWidth = 3f
                rightWidth = 3f
            }
        }
        label(name = Labels.TITLE.labelName){
            font = skin[Fonts.BIG]
            background = skin[Drawables.FRAME_FGD]
        }
        label(name = Labels.LARGE.labelName){
            font = skin[Fonts.BIG]
        }
        textButton(name = Buttons.TEXT_BUTTON.skinKey){
            font = skin[Fonts.DEFAULT].apply {
                data.setScale(0.2f)
            }
        }
    }
}

fun disposeSkin(){
    Scene2DSkin.defaultSkin.disposeSafely()
}
