package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3}
import com.publicscript.qucore.Resources.{model_pickup_box}


abstract class EntityPickup(world:World, pos:Vec3) extends Entity(world, pos) {

  this.model = Some(model_pickup_box)
  this.size = vec3(12, 12, 12)
  this.yaw += Math.PI / 2

  def pickup(): Unit

  override def update() = {
    if (!this.on_ground) {
      this.update_physics()
    }
    this.draw_model()
    if (get_distance_to_player() < 40) {
      this.pickup()
    }
  }

}

