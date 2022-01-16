package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3_dist}
import com.publicscript.qucore.Game.{game_next_level,game_entity_player}


class EntityTriggerLevel(apos:Vec3) extends Entity(apos) {

  override def update() = {
    if (!this.dead && vec3_dist(this.pos, game_entity_player.pos) < 64) {
      game_next_level()
      this.dead = true
    }
  }

}

