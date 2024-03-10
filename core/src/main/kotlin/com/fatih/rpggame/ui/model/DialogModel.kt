package com.fatih.rpggame.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.dialog.Dialog
import com.fatih.rpggame.event.EntityDialogEvent

class DialogModel(
    stage:Stage,
) : EventListener {

    private lateinit var dialog: Dialog
    private var text : String = ""
    private var completed : Boolean = false
    private var options : List<DialogOptionModel> = mutableListOf()
    var onTextChange : ((String) -> Unit)? = null
    var onDialogOptionChange :((List<DialogOptionModel>) -> Unit)? = null
    var onCompletedChange : ((Boolean) -> Unit)? = null

    init {
        stage.addListener(this)
    }

    fun triggerOption(optionId : Int){
        dialog.triggerOption(optionId)
        updateTextAndOptions()
    }

    private fun updateTextAndOptions(){
        completed = dialog.complete
        if (!completed){
            text = dialog.currentNode.text
            options = dialog.currentNode.options.map {
                DialogOptionModel(it.id,it.text)
            }
        }
        onTextChange?.invoke(text)
        onDialogOptionChange?.invoke(options)
        onCompletedChange?.invoke(completed)
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is EntityDialogEvent ->{
                this.dialog = event.dialog
                updateTextAndOptions()
                return true
            }
        }
        return false
    }
}
