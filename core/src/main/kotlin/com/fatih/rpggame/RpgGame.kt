package com.fatih.rpggame

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.fatih.rpggame.screens.GameScreen
import com.fatih.rpggame.screens.UiScreen
import com.fatih.rpggame.ui.disposeSkin
import com.fatih.rpggame.ui.loadSkin
import ktx.app.KtxGame
import ktx.app.KtxScreen

class RpgGame : KtxGame<KtxScreen>(){

    private val spriteBatch by lazy {
        SpriteBatch()
    }

    override fun create() {
        loadSkin()
        addScreen(GameScreen(spriteBatch,::setScreen))
        //addScreen(UiScreen())
        setScreen<GameScreen>()
    }

    companion object{
        const val UNIT_SCALE = 1/16f
    }

    override fun dispose() {
        disposeSkin()
        super.dispose()
    }
}
