package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Main.{model_gib,sfx_enemy_hit}
import com.publicscript.qucore.Game.{game_time}

class EntityProjectileGib(apos:Vec3) extends Entity(apos) {

  this.texture = Some(18)
  this.bounciness = 0
  this.die_at = game_time + 2
  this.model = Some(model_gib)
  this.yaw = Math.random()
  this.pitch = Math.random()

  override def update() = {
    super.update_physics()
    this.draw_model()
    this.f = if (this.on_ground) 15 else 0
  }

  override def did_collide(axis: Double) : Unit = {
    if (axis == 1 && this.veloc.y < -128) {
      this.play_sound(sfx_enemy_hit)
    }
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    other.receive_damage(this, 10)
    this.kill()
  }

}


