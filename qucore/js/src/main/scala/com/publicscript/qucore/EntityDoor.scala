package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_dist,scale,vec3_add,vec3_clone,vec3_rotate_y}
import com.publicscript.qucore.Game.{game_entities_enemies,game_entities_friendly,game_time,game_map_index,game_entity_player,game_show_message,game_tick}
import com.publicscript.qucore.Resources.{model_door}


class EntityDoor(apos:Vec3, tex:Int, dir:Double) extends Entity(apos) {

    this.model = Some(model_door)
    this.texture = Some(tex)
    this.health = 10
    this.size = vec3(64, 64, 64)
    var start_pos = vec3_clone(this.pos)
    var reset_state_at = 0d
    this.yaw = dir * Math.PI / 2
    var open = false
    // Map 1 only has one door and it needs a key. Should be a flag
    // in the entity data instead :/
    needs_key = game_map_index == 1
    // Doors block enemies and players
    game_entities_enemies.addOne(this)
    game_entities_friendly.addOne(this)

  override def update():Unit = {
    this.draw_model()
    if (vec3_dist(this.pos, game_entity_player.pos) < 128) {
      if (this.needs_key) {
        game_show_message("YOU NEED THE KEY...")
        return
      }
      this.reset_state_at = game_time + 3
    }
    if (this.reset_state_at < game_time) {
      this.open = false // Math.max(0, this.open - game_tick)
    } else {
      this.open = true // Math.min(1, this.open + game_tick)
    }
    this.pos = vec3_add(this.start_pos, vec3_rotate_y(vec3(if(this.open) 96 else 0, 0, 0), this.yaw))
  }

  def receive_damage() = {
  }

}

