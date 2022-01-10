package com.publicscript.qucore

import com.publicscript.qucore.Document.ts

object Game {

  var game_tick = 0
  var game_time = 0.016
  var game_real_time_last: Double = _
  var game_message_timeout = 0
  var game_entities = Array.empty[type]
  var game_entity_player = _

  def game_init(map_index: Double) = {
    {
      ts.style.display = "none"
      game_entities = Array()
    }
    val game_entities_enemies = Array.empty[Unit]
    val game_entities_friendly = Array.empty[Unit]
    val game_map_index = map_index
    map_init(map_data(game_map_index))
  }

  def game_next_level() = {
    val game_jump_to_next_level = 1
  }

  def game_spawn(`type`: Any, pos: Any, p1: Any, p2: Any) = {
    val entity = new `type`(pos, p1, p2)
    game_entities.push(entity)
    entity
  }

  def game_show_message(text: Any) = {
    msg.textContent = text
    msg.style.display = "block"
    clearTimeout(game_message_timeout)
    game_message_timeout = setTimeout(() => msg.style.display = "none"
      , 2000)
  }

  def title_show_message(msg: String, sub: String = "") = {
    ts.innerHTML = "<h1>" + msg + "</h1>" + sub
    ts.style.display = "block"
  }

  def game_run(time_now_par: Double) = {
    var time_now = time_now_par
    requestAnimationFrame(game_run)
    time_now *= 0.001
    game_tick = Math.min(time_now - (game_real_time_last || time_now), 0.05)
    game_real_time_last = time_now
    game_time += game_tick
    r_prepare_frame(0.1, 0.2, 0.5)
    // Update and render entities
    val alive_entities = Array.empty[type]
    /* Unsupported: ForOfStatement */ for (let entity of game_entities) {
      if (!entity._dead) {
        entity._update();
        alive_entities.push(entity);
      }
    }
    game_entities = alive_entities
    map_draw()
    r_end_frame()
    // Reset mouse movement and buttons that should be pressed, not held.
    mouse_x = mouse_y = 0
    keys(key_next) = keys(key_prev) = 0
    if (game_jump_to_next_level) {
      game_jump_to_next_level = 0
      game_map_index += 1
      if (game_map_index == 2) {
        title_show_message("THE END", "THANKS FOR PLAYING ❤")
        h.textContent = a.textContent = ""
        game_entity_player._dead = 1
        // Set camera position for end screen
        r_camera = vec3(1856, 784, 2272)
        r_camera_yaw = 0
        r_camera_pitch = 0.5
      } else {
        game_init(game_map_index)
      }
    }
  }

}
