package com.fatih.rpggame.ecs.playerai

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram


interface BaseState : State<PlayerEntity> {
    override fun enter(entity: PlayerEntity) {

    }

    override fun update(entity: PlayerEntity) {

    }

    override fun exit(entity: PlayerEntity) {

    }

    override fun onMessage(entity: PlayerEntity?, telegram: Telegram?): Boolean {
        return false
    }

}
