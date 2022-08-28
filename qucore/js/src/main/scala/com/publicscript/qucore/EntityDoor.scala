package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_dist,scale,vec3_add,vec3_clone,vec3_rotate_y}
import com.publicscript.qucore.Resources.{model_door}

class EntityDoor(world:World, apos:Vec3, tex:Int, dir:Double) extends Entity(world, apos) {

  this.model = Some(model_door)
  this.texture = Some(tex)
  this.health = 10
  this.size = vec3(64, 64, 64)
  this.yaw = dir * Math.PI / 2

  // Map 1 only has one door and it needs a key. Should be a flag
  // in the entity data instead :/
  this.needs_key = world.map_index == 1
  // Doors block enemies and players
  this.is_enemy = true
  this.is_friend = true

  var start_pos = vec3_clone(this.apos)
  var reset_state_at = 0d
  var open = false

  override def update(): Unit = {

    this.draw_model()
    if (get_distance_to_player() < 128) {
      if (this.needs_key) {
        world.show_game_message("YOU NEED THE KEY...")
        return
      }
      this.reset_state_at = world.time + 3
    }
    if (this.reset_state_at < world.time) {
      this.open = false
    } else {
      this.open = true
    }

    this.pos = vec3_add(this.start_pos, vec3_rotate_y(vec3(if (this.open) 96 else 0, 0, 0), this.yaw))
  }

  override def receive_damage(from: Entity, amount: Double): Unit = {
    //door never receives damage
  }

}

