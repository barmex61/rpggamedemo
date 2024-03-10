package com.fatih.rpggame.ecs.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.actor.FlipImage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity

class ImageComponent {

    lateinit var image : FlipImage

    companion object{

        class ImageComponentListener(
           private val gameStage:Stage
        ) : ComponentListener<ImageComponent>{

            override fun onComponentAdded(entity: Entity, component: ImageComponent) {
                gameStage.addActor(component.image)
            }

            override fun onComponentRemoved(entity: Entity, component: ImageComponent) {
                gameStage.root.removeActor(component.image)
            }
        }
    }
}
