package com.fatih.rpggame.dialog

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DialogTest{

    @Test
    fun testDialogDsl(){
        lateinit var firstNode : Node
        lateinit var secondNode : Node
        val testDialog = dialog("testDialog"){
            firstNode = node(0,"Node 0 txt"){
                option("next"){
                    action = { this@dialog.goToNode(1)}
                }
            }
            secondNode = node(1,"Node 1 txt"){
                option("previous"){
                    action = {this@dialog.goToNode(0) }
                }
                option("end") {
                    action = {this@dialog.endDialog()}
                }
            }
        }
        testDialog.start()
        assertEquals(firstNode,testDialog.currentNode)
        testDialog.triggerOption(0)
        assertEquals(secondNode,testDialog.currentNode)
        testDialog.triggerOption(1)
        assertTrue(testDialog.complete)
    }
}
