package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}
import com.publicscript.qucore.Resources.{model_explosion}

class EntityProjectileShell(world:World, apos:Vec3) extends Entity(world, apos) {

  this.gravity = 0
  this.die_at = world.time + 0.1


  override def update() = {
    this.update_physics()
  }

  override def did_collide(axis: Int):Unit = {
    this.kill()
    this.spawn_particles(2, 80, model_explosion, 4, 0.4)
    world.spawn("light", this.pos, 0.5, 0xff, lifetime=0.1)
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    this.kill()
    other.receive_damage(this, 4)
  }

}