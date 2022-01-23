package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}
import com.publicscript.qucore.Resources.{model_nail,model_explosion,sfx_nailgun_hit}
import com.publicscript.qucore.Game.{game_time,game_spawn}

class EntityProjectileNail(apos:Vec3) extends Entity(apos) {

  this.texture = Some(2)
  this.model = Some(model_nail)
  this.gravity = 0
  this.die_at = game_time + 3

  override def update() = {
    this.update_physics()
    this.draw_model()
  }

  override def did_collide(axis: Double):Unit = {
    this.kill()
    this.play_sound(sfx_nailgun_hit)
    this.spawn_particles(2, 80, model_explosion, 8, 0.4)
    game_spawn("light", this.pos, 1, 0xff).die_at = game_time + 0.1
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    this.kill()
    other.receive_damage(this, 9)
  }

}


