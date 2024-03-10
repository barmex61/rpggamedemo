package com.fatih.rpggame.ui.view

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.fatih.rpggame.ui.Drawables
import com.fatih.rpggame.ui.Labels
import com.fatih.rpggame.ui.widget.characterInfo
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.table
import com.fatih.rpggame.ui.get
import com.fatih.rpggame.ui.model.GameModel
import com.fatih.rpggame.ui.widget.CharacterInfo
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.label

class GameView(
    skin: Skin = Scene2DSkin.defaultSkin,
    gameModel: GameModel
) : KTable , Table() {

    private val playerInfo : CharacterInfo
    private val enemyInfo : CharacterInfo
    private val popUpLabel : Label

    init {
        setFillParent(true)
        enemyInfo = characterInfo(skin,Drawables.SLIME){
            it.row()
            this.alpha = 0f
        }
        table {
            background = skin[Drawables.FRAME_BGD]
            this@GameView.popUpLabel = label("This is a test new test hello test",Labels.FRAME.labelName){
                wrap = true
                it.expandX().fill().minHeight(100f).top().pad(8f)
            }
            it.expandY().height(150f).width(150f).row()
            this.alpha = 0f
        }
        playerInfo = characterInfo(skin,Drawables.PLAYER)
        gameModel.slimeLifeCallback = {
            showEnemyInfo(Drawables.SLIME,it)
        }
        gameModel.playerLifeCallback = {
            playerLife(it)
        }
        gameModel.lootTextCallback = {
            popup(it)
        }
    }

    fun playerLife(percentage:Float){
        playerInfo.changeLife(percentage)
    }

    fun showEnemyInfo(charDrawable: Drawables, lifePercentage:Float){
        enemyInfo.character(charDrawable)
        enemyInfo.changeLife(lifePercentage,1f)
        enemyInfo.clearActions()
        enemyInfo += delay(1f)
        enemyInfo += Actions.sequence(
            fadeIn(1f),
            delay(3f, fadeOut(0.5f))
        )
    }

    fun popup(infoText:String){
        popUpLabel.setText(infoText)
        popUpLabel.parent.clearActions()
        popUpLabel.parent += Actions.sequence(
            fadeIn(1f),
            delay(3f, fadeOut(0.75f))
        )
    }

}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    skin : Skin = Scene2DSkin.defaultSkin,
    gameModel : GameModel,
    init : GameView.(S) -> Unit = {}
) : GameView = actor(GameView(skin,gameModel),init)
