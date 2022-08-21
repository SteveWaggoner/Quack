package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3_dist}
import com.publicscript.qucore.Game.{game_next_level}


class EntityTriggerLevel(world:World, apos:Vec3) extends Entity(world, apos) {

  override def update() = {
    if (!this.dead && get_distance_to_player() < 64) {
      game_next_level()
      this.dead = true
    }
  }

}

