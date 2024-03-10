package com.fatih.rpggame.ecs.system

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.fatih.rpggame.ecs.component.AnimationComponent
import com.fatih.rpggame.ecs.component.AttackComponent
import com.fatih.rpggame.ecs.component.DialogComponent
import com.fatih.rpggame.ecs.component.ImageComponent
import com.fatih.rpggame.ecs.component.LifeComponent
import com.fatih.rpggame.ecs.component.LootComponent
import com.fatih.rpggame.ecs.component.MoveComponent
import com.fatih.rpggame.ecs.component.PhysicComponent
import com.fatih.rpggame.ecs.component.PhysicComponent.Companion.COLLISION_BOX
import com.fatih.rpggame.ecs.component.PlayerComponent
import com.fatih.rpggame.ecs.system.PhysicSystem.Companion.entity
import com.fatih.rpggame.event.EntityAttackEvent
import com.fatih.rpggame.event.fireEvent
import com.fatih.rpggame.utils.AttackState
import com.fatih.rpggame.utils.Direction
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.box2d.query
import ktx.box2d.rayCast
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

@AllOf([AttackComponent::class])
class AttackSystem(
    private val attackComps : ComponentMapper<AttackComponent>,
    private val physicComps : ComponentMapper<PhysicComponent>,
    private val animComps : ComponentMapper<AnimationComponent>,
    private val moveComps : ComponentMapper<MoveComponent>,
    private val imageComps : ComponentMapper<ImageComponent>,
    private val lifeComps : ComponentMapper<LifeComponent>,
    private val lootComps : ComponentMapper<LootComponent>,
    private val dialogComps : ComponentMapper<DialogComponent>,
    private val gameStage : Stage,
    private val box2dWorld : World
) : IteratingSystem(){

    override fun onTickEntity(entity: Entity) {
        val attackComponent = attackComps[entity]
        attackComponent.run {
            if (attackState == AttackState.READY){
                return
            }
            if (attackState == AttackState.PREPARE) {
                if (attackState == AttackState.PREPARE) {
                    gameStage.fireEvent(EntityAttackEvent(animComps[entity].entityModel))
                    val flipX = imageComps[entity].image.flipX
                    attackState = AttackState.ATTACK
                    val (posX, posY) = physicComps[entity].body.position
                    val (width, height) = physicComps[entity].bodySize
                    val (offX, offY) = physicComps[entity].bodyOffset
                    val direction = moveComps[entity].direction
                    when (direction) {
                        Direction.RIGHT -> {
                            ATTACK_RECT.set(
                                posX + offX + if (!flipX) -width / 2f else -3 * width / 2f,
                                posY + offY - height * 1.8f / 3f,
                                width * 2f,
                                height
                            )
                        }

                        Direction.FRONT -> {
                            ATTACK_RECT.set(
                                posX + offX - width / 1.5f,
                                posY + offY - height,
                                width * 1.5f,
                                height
                            )
                        }

                        Direction.BACK -> {
                            ATTACK_RECT.set(
                                posX + offX - width / 1.5f,
                                posY + offY,
                                width * 1.5f,
                                height
                            )
                        }
                    }
                }
            }
            if (attackState == AttackState.ATTACK){
                box2dWorld.query(
                    ATTACK_RECT.x, ATTACK_RECT.y, ATTACK_RECT.x + ATTACK_RECT.width, ATTACK_RECT.y + ATTACK_RECT.height
                ){fixture ->
                    val fixtureEntity = fixture.entity()
                    if (fixture.userData != COLLISION_BOX) return@query true
                    if (fixtureEntity == entity) return@query true
                    lifeComps.getOrNull(fixtureEntity)?.apply {
                        damageTaken += attackDamage.random() * if ((0..100).random() <= criticalChance) {
                            isCrit = true
                            2f
                        } else {
                            isCrit = false
                            1f
                        }
                    }
                    dialogComps.getOrNull(fixtureEntity)?.interractEntity = entity
                    lootComps.getOrNull(fixtureEntity)?.isLooted = true
                    return@query true
                }

                attackState = AttackState.DONE
            }
            if (attackState == AttackState.DONE && animComps[entity].isAnimationDone){
                doAttack = false
                attackState = AttackState.READY
            }
        }
    }

    companion object{
        val ATTACK_RECT = Rectangle()
    }
}
