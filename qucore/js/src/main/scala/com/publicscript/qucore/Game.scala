package com.publicscript.qucore

import com.publicscript.qucore.Document.ts
import com.publicscript.qucore.Map.{MapEntity,map_init,map_draw}
import com.publicscript.qucore.MathUtils.{Vec3, vec3}

import scala.collection.mutable.ArrayBuffer

import com.publicscript.qucore.Resources.map_data
import com.publicscript.qucore.Document.msg

import com.publicscript.qucore.Render.{r_prepare_frame,r_end_frame,r_camera,r_camera_yaw,r_camera_pitch}

import com.publicscript.qucore.Document.{h,a}

import com.publicscript.qucore.Input.{mouse_x,mouse_y,key_prev,key_next}

import scala.scalajs.js.timers._

import org.scalajs.dom.window.requestAnimationFrame



object Game {

  var game_tick = 0d
  var game_time = 0.016d
  var game_real_time_last: Double = _
  var game_message_timeout : SetTimeoutHandle = _
  var game_entities = new ArrayBuffer[Entity](0)
  var game_entity_player: EntityPlayer = _


  var game_entities_enemies = new ArrayBuffer[Entity](0)
  var game_entities_friendly = new ArrayBuffer[Entity](0)
  var game_map_index = -1

  var game_jump_to_next_level = false


  def game_init(map_index: Int) = {
    {
      println(ts)

      ts.style.display = "none"
      game_entities.clear()
    }
    game_map_index = map_index

    map_init(map_data(game_map_index))
  }

  def game_next_level() = {
    val game_jump_to_next_level = 1
  }

  def game_spawn(entity_name:String, pos:Vec3, data1:Any = null, data2:Any = null) : Entity = {
    val entity = Entity(entity_name, pos, data1, data2)
    game_entities.addOne(entity)
    entity
  }


  def game_show_message(text: String) = {
    msg.textContent = text
    msg.style.display = "block"
    clearTimeout(game_message_timeout)
    game_message_timeout = setTimeout(2000) { msg.style.display = "none" }
  }

  def title_show_message(msg: String, sub: String = "") = {
    ts.innerHTML = "<h1>" + msg + "</h1>" + sub
    ts.style.display = "block"
  }

  def game_run(time_now_par: Double) : Unit = {
    var time_now = time_now_par

    requestAnimationFrame(game_run)
    time_now *= 0.001

    if ( game_real_time_last == 0)
      game_real_time_last = time_now

    game_tick = Math.min(time_now - game_real_time_last, 0.05)
    game_real_time_last = time_now
    game_time += game_tick
    r_prepare_frame(0.1, 0.2, 0.5)
    // Update and render entities
    val alive_entities = new ArrayBuffer[Entity](0)
    for (entity <- game_entities) {
      if (!entity.dead) {
        entity.update()
        alive_entities.addOne(entity);
      }
    }
    game_entities = alive_entities
    map_draw()
    r_end_frame()
    // Reset mouse movement and buttons that should be pressed, not held.
    mouse_x = 0
    mouse_y = 0
    key_next = false
    key_prev = false
    if (game_jump_to_next_level) {
      game_jump_to_next_level = false
      game_map_index += 1
      if (game_map_index == 2) {
        title_show_message("THE END", "THANKS FOR PLAYING ❤")
        h.textContent = ""
        a.textContent = ""
        game_entity_player.dead = true
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
