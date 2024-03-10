package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.dialog.Dialog
import com.fatih.rpggame.dialog.dialog
import com.fatih.rpggame.ecs.component.DialogComponent
import com.fatih.rpggame.ecs.component.DialogId
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.event.EntityDialogEvent
import com.fatih.rpggame.event.fireEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError

@AllOf([DialogComponent::class])
class DialogSystem(
    private val dialogComps : ComponentMapper<DialogComponent>,
    private val moveComps : ComponentMapper<MoveComponent>,
    private val gameStage : Stage
) : IteratingSystem(){

    private val dialogCache = mutableMapOf<DialogId,Dialog>()

    override fun onTickEntity(entity: Entity) {
        val dialogComponent = dialogComps[entity]
        dialogComponent.run {
            val triggerEntity = interractEntity ?: return
            var dialog = currentDialog
            if (dialog != null ){
                if (dialog.complete){
                    moveComps.getOrNull(triggerEntity)?.let { it.root = false }
                    currentDialog = null
                    interractEntity = null
                }
                return
            }
            dialog = getDialog(dialogId).also{it.start()}
            currentDialog = dialog
            moveComps.getOrNull(triggerEntity)?.let { it.root = true }
            gameStage.fireEvent(EntityDialogEvent(dialog))
        }
    }

    private fun getDialog(dialogId : DialogId): Dialog {
        return dialogCache.getOrPut(dialogId){
            when(dialogId){
                DialogId.BLOB ->{
                    dialog(dialogId.name){
                        node(0,"Hello adventurer ! Can u please take care of my blue brothers"){
                            option("But Why"){
                                action = { this@dialog.goToNode(1)}
                            }
                        }
                        node(1,"A dark magic has possessed them . There is no cure - KILL THEM ALL"){
                            option("Again?"){
                                action = {this@dialog.goToNode(0) }
                            }
                            option("Ok,ok!") {
                                action = {this@dialog.endDialog()}
                            }
                        }
                    }
                }
                else -> gdxError("Errorrr")
            }
        }
    }

}
