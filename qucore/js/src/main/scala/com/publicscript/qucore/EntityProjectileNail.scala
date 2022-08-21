package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}
import com.publicscript.qucore.Resources.{model_nail,model_explosion,sfx_nailgun_hit}

class EntityProjectileNail(world:World, apos:Vec3) extends Entity(world,apos) {

  this.texture = Some(2)
  this.model = Some(model_nail)
  this.gravity = 0
  this.die_at = world.time + 3

  override def update() = {
    this.update_physics()
    this.draw_model()
  }

  override def did_collide(axis: Int):Unit = {
    this.kill()
    this.play_sound(sfx_nailgun_hit)
    this.spawn_particles(2, 80, model_explosion, 8, 0.4)
    world.spawn("light", this.pos, 1, 0xff, lifetime=0.1)
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    this.kill()
    other.receive_damage(this, 9)
  }

}


