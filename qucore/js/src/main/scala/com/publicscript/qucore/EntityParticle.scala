package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3


class EntityParticle(world:World, pos: Vec3) extends Entity(world, pos) {
  bounciness = 0.5
  friction = 0.1

  override def update() = {
    this.yaw += this.veloc.y * 0.001
    this.pitch += this.veloc.x * 0.001
    this.update_physics()
    this.draw_model()
  }

}


