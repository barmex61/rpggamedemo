package com.fatih.rpggame.dialog

import ktx.app.gdxError

fun dialog(id:String,config:Dialog.() -> Unit) : Dialog {
    return Dialog(id).apply(config)
}

data class Dialog(val id:String,var complete:Boolean = false, private val nodes : MutableList<Node> = mutableListOf()){

    lateinit var currentNode: Node

    fun node(id:Int,text:String,config:Node.() -> Unit) : Node {
       return Node(id, text).apply {
           this.config()
           nodes += this
       }
    }
    fun goToNode(id:Int){
        currentNode = nodes.firstOrNull { it.id == id } ?: gdxError("There is no node id for $id")
    }

    fun start(){
        complete = false
        currentNode = nodes.first()
    }

    fun endDialog(){
        complete = true
    }

    fun triggerOption(id:Int) {
        val option = currentNode[id] ?: gdxError("Option index failed")
        option.action()
    }
}

data class Node(val id : Int,val text:String,val options : MutableList<Option> = mutableListOf()){
    fun option(text: String,config:Option.() -> Unit) : Option{
        return Option(options.size,text).apply {
            config()
            options += this
        }
    }

    operator fun get(id:Int) : Option?{
        return options.getOrNull(id)
    }
}

data class Option(val id : Int,val text: String,var action :() -> Unit = {})
