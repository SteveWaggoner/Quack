package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}

class EntityTriggerLevel(world:World, apos:Vec3) extends Entity(world, apos) {

  override def update() = {
    if (!this.dead && get_distance_to_player() < 64) {
      world.next_level()
      this.dead = true
    }
  }

}

