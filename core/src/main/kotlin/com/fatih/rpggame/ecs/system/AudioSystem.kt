package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.fatih.rpggame.event.EnemyDeathEvent
import com.fatih.rpggame.event.EntityAttackEvent
import com.fatih.rpggame.event.GamePauseEvent
import com.fatih.rpggame.event.GameResumeEvent
import com.fatih.rpggame.event.LootEvent
import com.fatih.rpggame.event.MapChangeEvent
import com.fatih.rpggame.event.PlayerDeathEvent
import com.fatih.rpggame.utils.AudioPath
import com.fatih.rpggame.utils.EntityModel
import com.github.quillraven.fleks.IntervalSystem
import ktx.tiled.property

class AudioSystem : IntervalSystem() , EventListener {

    private val soundCache = mutableMapOf<String,Sound>()
    private val musicCache = mutableMapOf<String,Music>()
    private var music : Music ? = null

    override fun onTick() {

    }

    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent ->{
                val path = event.map.property<String>("mapMusic")
                val newMusic = musicCache.getOrPut(path){
                    Gdx.audio.newMusic(Gdx.files.internal(path))
                }
                if (music == null || music != newMusic){
                    music?.stop()
                    music = newMusic
                    music!!.play()
                }
            }
            is EntityAttackEvent ->{
                val path = if (event.entityModel == EntityModel.PLAYER) AudioPath.PLAYER_ATTACK.path else AudioPath.ENEMY_ATTACK.path
                soundCache.getOrPut(path){
                    Gdx.audio.newSound(Gdx.files.internal(path))
                }.play()
            }

            is PlayerDeathEvent ->{
                val path = AudioPath.PLAYER_DEATH.path
                soundCache.getOrPut(path){
                    Gdx.audio.newSound(Gdx.files.internal(path))
                }.play()
            }
            is EnemyDeathEvent ->{
                val path = AudioPath.ENEMY_DEATH.path
                soundCache.getOrPut(path){
                    Gdx.audio.newSound(Gdx.files.internal(path))
                }.play()
            }
            is LootEvent ->{
                val path = AudioPath.LOOT.path
                soundCache.getOrPut(path){
                    Gdx.audio.newSound(Gdx.files.internal(path))
                }.play()
            }

            is GamePauseEvent -> {
                music?.pause()
                soundCache.values.forEach { it.pause() }
            }
            is GameResumeEvent -> {
                music?.play()
                soundCache.values.forEach { it.resume() }
            }

        }
        return false
    }
}
