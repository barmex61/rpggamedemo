package com.fatih.rpggame.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.dialog.Dialog
import com.fatih.rpggame.utils.EntityModel
import com.github.quillraven.fleks.Entity

fun Stage.fireEvent(event : Event) = this.root.fire(event)

data class MapChangeEvent(val map : TiledMap) : Event()
data class CollisionDespawnEvent(val cell : Cell) : Event()
class EntityAttackEvent (val entityModel: EntityModel) : Event()
class PlayerDeathEvent : Event()
class EnemyDeathEvent : Event()
class LootEvent(val lootText : String) : Event()
class PlayerDamageEvent(val lifePercent : Float) : Event()
class EnemyDamageEvent(val lifePercent: Float) : Event()
class EntityAddItemEvent(val entity:Entity,val item : Entity) : Event()
class GamePauseEvent : Event()
class GameResumeEvent : Event()
class EntityDialogEvent(val dialog: Dialog) : Event()
