package com.fatih.rpggame.utils

import com.fatih.rpggame.ui.Drawables
import ktx.app.gdxError

enum class MapLayerType{
    BACKGROUND,GROUND,FOREGROUND,ENTITY;

    val layerName = this.name.lowercase()
}

enum class ItemCategory(val drawables: Drawables){
    UNDEFINED(Drawables.UNDEFINED),
    HELMET(Drawables.HELMET),
    ARMOR(Drawables.ARMOR),
    WEAPON(Drawables.WEAPON1),
    BOOTS(Drawables.BOOTS)
}

enum class Direction{
    FRONT,BACK,RIGHT
}

enum class AttackState{
    READY,PREPARE,ATTACK,DONE
}

enum class AudioPath(val path : String){
    PLAYER_ATTACK("audio/player_attack.wav"),
    PLAYER_DEATH("audio/player_death.wav"),
    ENEMY_ATTACK("audio/slime_attack.wav"),
    ENEMY_DEATH("audio/slime_death.wav"),
    LOOT("audio/chest_open.wav")
}

enum class EntityModel{
    PLAYER,SLIME,CHEST,BLOB;
    val entityName = this.name.lowercase()
}

enum class AnimationType{
    IDLE,ATTACK,RUN,DEATH,OPEN,IDLE_FRONT,IDLE_BACK,IDLE_RIGHT,RUN_FRONT,RUN_RIGHT,RUN_BACK,
    ATTACK_FRONT,ATTACK_BACK,ATTACK_RIGHT;

    val animationName = this.name.lowercase()

    fun animationTypeWithDirection(direction : Direction) : AnimationType{
       val directionSuffix = when(direction){
            Direction.FRONT ->"_FRONT"
            Direction.BACK ->"_BACK"
            Direction.RIGHT ->"_RIGHT"
        }
        return try {
            valueOf("$name$directionSuffix")
        }catch (e:Exception){
            gdxError("Animation type $name with direction $directionSuffix not match")
        }
    }
}
