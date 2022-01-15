package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3


class EntityParticle(pos: Vec3) extends Entity(pos) {
  bounciness = 0.5
  f = 0.1

  override def update() = {
    this.yaw += this.veloc.y * 0.001
    this.pitch += this.veloc.x * 0.001
    this.update_physics()
    this.draw_model()
  }

}


