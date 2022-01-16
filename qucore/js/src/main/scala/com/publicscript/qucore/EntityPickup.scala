package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_dist}
import com.publicscript.qucore.Main.{model_pickup_box,sfx_enemy_hit}
import com.publicscript.qucore.Game.{game_entity_player}



abstract class EntityPickup(apos:Vec3) extends Entity(apos) {


  this.model = Some(model_pickup_box)
  this.size = vec3(12, 12, 12)
  this.yaw += Math.PI / 2

  def pickup():Unit

  override def update() = {
    if (!this.on_ground) {
      this.update_physics()
    }
    this.draw_model()
    if (vec3_dist(this.pos, game_entity_player.pos) < 40) {
      this.pickup()
    }
  }

}

