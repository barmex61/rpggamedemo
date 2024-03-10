package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import com.fatih.rpggame.ecs.component.AiComponent
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.AI_COLLISION_CIRCLE
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.COLLISION_BOX
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.COLLISION_CIRCLE_OBJECT
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.HIT_BOX
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.PORTAL_OBJECT
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.ecs.component.PortalComponent
import com.fatih.rpggame.ecs.component.TiledComponent
import com.fatih.rpggame.utils.Constants
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import ktx.math.component1
import ktx.math.component2


@AllOf([PhysicComponent::class,ImageComponent::class])
class PhysicSystem(
    private val physicComps : ComponentMapper<PhysicComponent>,
    private val box2dWorld: World,
    private val imageComps : ComponentMapper<ImageComponent>,
    private val tiledComps : ComponentMapper<TiledComponent>,
    private val aiComps : ComponentMapper<AiComponent>,
    private val portalComps : ComponentMapper<PortalComponent>
) : IteratingSystem(interval = Fixed(1/60f)) , ContactListener {

    init {
        box2dWorld.setContactListener(this)
    }

    override fun onUpdate() {
        if (box2dWorld.autoClearForces){
            box2dWorld.autoClearForces = false
        }
        super.onUpdate()
        box2dWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        box2dWorld.step(deltaTime,6,2)
    }

    override fun onTickEntity(entity: Entity) {

        physicComps[entity].run {
            previousPos.set(body.position)
            if (!impulse.isZero){
                body.applyLinearImpulse(impulse,body.worldCenter,true)
                impulse.setZero()
            }
        }
    }

    override fun onAlphaEntity(entity: Entity, alpha: Float) {

        val physicComponent = physicComps[entity]
        val imageComponent = imageComps[entity]
        val (prevX,prevY) = physicComponent.previousPos
        val (bodyX,bodyY) = physicComponent.body.position
        imageComponent.image.apply {
            setPosition(
                MathUtils.lerp(prevX,bodyX,alpha) - width /2f
                ,MathUtils.lerp(prevY,bodyY,alpha) - height/2f)
        }
    }

    private fun removeNearbyEntity(entity: Entity,nearbyEntity: Entity){
        tiledComps.getOrNull(entity)?.let {
            it.nearbyEntities -= nearbyEntity
        }
    }



    companion object{
        fun Fixture.entity() = this.body.userData as Entity
    }

    override fun beginContact(contact: Contact) {

        if (contact.fixtureA.filterData.categoryBits == Constants.PLAYER ){
            if (contact.fixtureB.userData == AI_COLLISION_CIRCLE){
                aiComps.getOrNull(contact.fixtureB.entity())?.apply {
                    nearbyEntities += contact.fixtureA.entity()
                }
            }
            if (contact.fixtureB.userData == PORTAL_OBJECT && contact.fixtureA.userData == COLLISION_BOX){
                portalComps[contact.fixtureB.entity()].triggerEntities += contact.fixtureA.entity()
            }
        }
        if (contact.fixtureB.filterData.categoryBits == Constants.PLAYER ){
            if (contact.fixtureA.userData == AI_COLLISION_CIRCLE){
                aiComps.getOrNull(contact.fixtureA.entity())?.apply {
                    nearbyEntities += contact.fixtureB.entity()
                }
            }
            if (contact.fixtureA.userData == PORTAL_OBJECT && contact.fixtureB.userData == COLLISION_BOX){
                portalComps[contact.fixtureA.entity()].triggerEntities += contact.fixtureB.entity()
            }
        }
    }

    override fun endContact(contact: Contact) {
        if (contact.fixtureA.userData == COLLISION_CIRCLE_OBJECT && contact.fixtureB.userData == COLLISION_BOX){
            removeNearbyEntity(contact.fixtureA.entity(),contact.fixtureB.entity())
        }
        if (contact.fixtureB.userData == COLLISION_CIRCLE_OBJECT && contact.fixtureA.userData == COLLISION_BOX){
            removeNearbyEntity(contact.fixtureB.entity(),contact.fixtureA.entity())
        }
        if (contact.fixtureA.filterData.categoryBits == Constants.PLAYER){
            if (contact.fixtureB.userData == AI_COLLISION_CIRCLE){
                aiComps.getOrNull(contact.fixtureB.entity())?.apply {
                    nearbyEntities -= contact.fixtureA.entity()
                }
            }
            if (contact.fixtureB.userData == PORTAL_OBJECT && contact.fixtureA.userData == COLLISION_BOX){
                portalComps[contact.fixtureB.entity()].triggerEntities -= contact.fixtureA.entity()
            }
        }
        if (contact.fixtureB.filterData.categoryBits == Constants.PLAYER ){
            if (contact.fixtureA.userData == AI_COLLISION_CIRCLE){
                aiComps.getOrNull(contact.fixtureA.entity())?.apply {
                    nearbyEntities -= contact.fixtureB.entity()
                }
            }
            if (contact.fixtureA.userData == PORTAL_OBJECT && contact.fixtureB.userData == COLLISION_BOX){
                portalComps[contact.fixtureA.entity()].triggerEntities -= contact.fixtureB.entity()
            }
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) = Unit

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?)  = Unit

}
