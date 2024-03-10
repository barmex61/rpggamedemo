package com.fatih.rpggame.ecs.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.fatih.rpggame.ecs.enemyai.SlimeEntity
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

class AiComponent (
    val nearbyEntities : MutableSet<Entity> = mutableSetOf(),
    var treePath : String = ""
){
    lateinit var behaviorTree: BehaviorTree<SlimeEntity>

    companion object{
        class AiComponentListener(private val world: World) : ComponentListener<AiComponent>{

            private val treeParser = BehaviorTreeParser<SlimeEntity>()

            override fun onComponentAdded(entity: Entity, component: AiComponent) {
                component.behaviorTree = treeParser.parse(Gdx.files.internal(component.treePath), SlimeEntity(world,entity))
            }

            override fun onComponentRemoved(entity: Entity, component: AiComponent) {

            }
        }
    }
}
