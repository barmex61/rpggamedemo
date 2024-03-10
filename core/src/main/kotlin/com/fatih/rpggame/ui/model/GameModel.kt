package com.fatih.rpggame.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.event.EnemyDamageEvent
import com.fatih.rpggame.event.LootEvent
import com.fatih.rpggame.event.PlayerDamageEvent

class GameModel(
    stage : Stage
) : EventListener {

    var playerLifeCallback : ((Float) -> Unit)? = null
    var slimeLifeCallback : ((Float) -> Unit)? = null
    var lootTextCallback : ((String) -> Unit)? = null

    private var playerLife : Float = 1f
    private var slimeLife : Float = 1f
    private var lootText : String = ""

    override fun handle(event: Event?): Boolean {
        when(event){
            is PlayerDamageEvent -> {
                playerLife = event.lifePercent
                playerLifeCallback?.invoke(playerLife)
            }
            is EnemyDamageEvent ->{
                slimeLife = event.lifePercent
                slimeLifeCallback?.invoke(slimeLife)
            }
            is LootEvent ->{
                lootText = event.lootText
                lootTextCallback?.invoke(lootText)
            }
        }
        return false
    }

}
