package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_gib,sfx_enemy_hit}


class EntityProjectileGib(world:World, apos:Vec3) extends Entity(world, apos) {

  this.texture = Some(18)
  this.bounciness = 0
  this.die_at = world.time + 2
  this.model = Some(model_gib)
  this.yaw = Math.random()
  this.pitch = Math.random()

  override def update() = {
    super.update_physics()
    this.draw_model()
    this.friction = if (this.on_ground) 15 else 0
  }

  override def did_collide(axis: Int) : Unit = {
    if (axis == 1 && this.veloc.y < -128) {
      this.play_sound(sfx_enemy_hit)
    }
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    other.receive_damage(this, 10)
    this.kill()
  }

}


