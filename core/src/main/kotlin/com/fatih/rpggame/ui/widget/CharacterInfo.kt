package com.fatih.rpggame.ui.widget

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.fatih.rpggame.ui.Drawables
import ktx.scene2d.KGroup
import com.fatih.rpggame.ui.get
import ktx.actors.plusAssign
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor

class CharacterInfo(
    val skin: Skin,
    drawables: Drawables
) : KGroup , WidgetGroup() {

    private val background = Image(skin[Drawables.CHAR_INFO_BGD])
    private val lifeBar = Image(skin[Drawables.LIFE_BAR]).apply {
        setPosition(26f,19f)
    }
    private val manaBar = Image(skin[Drawables.MANA_BAR]).apply {
        setPosition(26f,12.8f)
    }
    private val profile = Image(skin[drawables]).apply {
        setPosition(4f,3f)
        setSize(18f,18f)
    }

    init {
        this += background
        this += lifeBar
        this += manaBar
        this += profile
    }


    override fun getPrefWidth() = background.drawable.minWidth

    override fun getPrefHeight() = background.drawable.minHeight

    fun character(charDrawables: Drawables?){
        if (charDrawables == null){
            profile.drawable = null
        }else{
            profile.drawable = skin[charDrawables]
        }
    }


    fun changeLife(percentage: Float,duration : Float = 0.75f) {
        lifeBar.clearActions()
        lifeBar += scaleTo(MathUtils.clamp(percentage,0f,1f),1f,duration, Interpolation.smoother)
    }
}

fun <S>KWidget<S>.characterInfo(
    skin: Skin = Scene2DSkin.defaultSkin,
    drawables: Drawables,
    init : CharacterInfo.(S)->Unit = {}
) : CharacterInfo = actor(CharacterInfo(skin, drawables),init)
