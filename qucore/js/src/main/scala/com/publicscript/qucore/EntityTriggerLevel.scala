package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}

class EntityTriggerLevel(world:World, pos:Vec3) extends Entity(world, pos) {

  override def update() = {
    if (!this.dead && get_distance_to_player() < 64) {
      world.next_level()
      this.dead = true
    }
  }

}

