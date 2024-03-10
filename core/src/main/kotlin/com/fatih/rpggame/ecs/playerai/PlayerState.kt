package com.fatih.rpggame.ecs.playerai

import com.badlogic.gdx.graphics.g2d.Animation
import com.fatih.rpggame.utils.AnimationType

enum class PlayerState : BaseState {
    IDLE{
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: PlayerEntity) {
            when{
                entity.wantsToRun -> entity.changeState(RUN)
                entity.wantsToAttack -> entity.changeState(ATTACK)
            }
        }
    },
    RUN{
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.RUN)
        }

        override fun update(entity: PlayerEntity) {
            when{
                entity.updateDirection -> entity.animation(AnimationType.RUN)
                !entity.wantsToRun -> entity.changeState(IDLE)
                entity.wantsToAttack -> entity.changeState(ATTACK)
            }
        }
    },
    ATTACK{
        override fun enter(entity: PlayerEntity) {
            entity.animation(AnimationType.ATTACK, playMode = Animation.PlayMode.NORMAL)
            entity.startAttack()
            entity.root(true)
        }

        override fun update(entity: PlayerEntity) {
            when{
                !entity.wantsToAttack && entity.attackReadyState -> entity.changePreviousState()
            }
        }

        override fun exit(entity: PlayerEntity) {
            entity.root(false)
        }
    },
    DEATH{

    },
    RESURRECT{

    }
}
