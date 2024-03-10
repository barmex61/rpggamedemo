package com.fatih.rpggame.ecs.system

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.fatih.rpggame.ecs.component.LightComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.IteratingSystem

@AllOf([LightComponent::class])
class LightSystem(
    private val rayHandler: RayHandler,
    private val lightComps : ComponentMapper<LightComponent>
) : IteratingSystem() {

    private var ambientTransitionTime = 1f
    private var ambientColor = Color(1f,1f,1f,1f)
    private var ambientColorFrom = dayAmbientLight
    private var ambientColorTo = nightAmbientLight

    override fun onTick() {
        super.onTick()
        if (Gdx.input.isKeyJustPressed(Input.Keys.N) && ambientTransitionTime == 1f){
            ambientTransitionTime = 0f
            ambientColorFrom = dayAmbientLight
            ambientColorTo = nightAmbientLight
        }else if (Gdx.input.isKeyPressed(Input.Keys.U) && ambientTransitionTime == 1f){
            ambientTransitionTime = 0f
            ambientColorFrom = nightAmbientLight
            ambientColorTo = dayAmbientLight
        }
        if (ambientTransitionTime < 1f){
            ambientTransitionTime = (ambientTransitionTime + deltaTime * 0.5f).coerceAtMost(1f)
            ambientColor.set(
                interpolation.apply(ambientColorFrom.r,ambientColorTo.r,ambientTransitionTime),
                interpolation.apply(ambientColorFrom.g,ambientColorTo.g,ambientTransitionTime),
                interpolation.apply(ambientColorFrom.b,ambientColorTo.b,ambientTransitionTime),
                1f
            )
            rayHandler.setAmbientLight(ambientColor)
        }
    }

    override fun onTickEntity(entity: Entity) {
        val lightComponent = lightComps[entity]
        lightComponent.run {
            distanceTime = (distanceTime + distanceDirection * deltaTime).coerceIn(0f,1f)
            if (distanceTime == 0f || distanceTime == 1f){
                distanceDirection *= -1
            }
            light.distance = interpolation.apply(distance.start,distance.endInclusive,distanceTime)
        }
    }

    companion object{
        private val interpolation = Interpolation.smoother
        private val dayAmbientLight = Color.WHITE
        private val nightAmbientLight = Color.ROYAL
    }
}
