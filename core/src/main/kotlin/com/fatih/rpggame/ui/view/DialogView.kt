package com.fatih.rpggame.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.fatih.rpggame.ui.Buttons
import com.fatih.rpggame.ui.Drawables
import com.fatih.rpggame.ui.Labels
import com.fatih.rpggame.ui.model.DialogModel
import ktx.actors.alpha
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.table
import com.fatih.rpggame.ui.get
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.textButton

class DialogView(
    private val dialogModel: DialogModel,
    skin : Skin
) : KTable , Table(){

    private val dialogText : Label
    private val buttonArea : Table

    init {
        setFillParent(true)
        alpha = 0f
        table {
            background = skin[Drawables.FRAME_BGD]
            this@DialogView.dialogText = label(text = "", style = Labels.FRAME.labelName){
                setAlignment(Align.topLeft)
                wrap = true
                it.expand().fill().pad(8f).row()
            }
            this@DialogView.buttonArea = table{
                this.defaults().expand()
                textButton("",Buttons.TEXT_BUTTON.skinKey)
                textButton("",Buttons.TEXT_BUTTON.skinKey)
                it.expandX().fillX().pad(0f,8f,8f,8f)
            }
            it.expand().width(200f).height(130f).center().row()
        }
        dialogModel.onDialogOptionChange = {
            buttonArea.clearChildren()
            it.forEach {
                buttonArea.add(textButton(it.text,Buttons.TEXT_BUTTON.skinKey).apply {
                    onClick { this@DialogView.dialogModel.triggerOption(it.id) }
                })
            }
        }
        dialogModel.onCompletedChange = {
            if (it){
                this.alpha = 0f
                this.buttonArea.clearChildren()
            }
        }

        dialogModel.onTextChange = {
            dialogText.setText(it)
            this.alpha = 1f
        }
    }

}

@Scene2dDsl
fun <S>KWidget<S>.dialogView(
    dialogModel : DialogModel,
    skin : Skin = Scene2DSkin.defaultSkin,
    init : DialogView.(S) -> Unit = {}
) : DialogView = actor(DialogView(dialogModel,skin),init)
