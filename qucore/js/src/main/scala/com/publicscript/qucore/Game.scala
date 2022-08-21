package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3, vec3}
import com.publicscript.qucore.Resources.map_data
import com.publicscript.qucore.Input.{mouse_x,mouse_y,key_prev,key_next}

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js.timers._


object Game {

  var world = new GameWorld()


  var game_message_timeout : SetTimeoutHandle = _

  var game_map_index = -1

  var game_jump_to_next_level = false

  val render = new Render(Document.c)
  val audio = new Audio()
  var map = new Map()
  var model = new Model(render)

  def game_init(map_index: Int) = {
    Document.ts.style.display = "none"
    //game_entities.clear()
    world.init()
    game_map_index = map_index
    map.init(map_data(game_map_index))
  }

  def game_next_level() = {
    println("game_jump_to_next_level = true")
    game_jump_to_next_level = true
  }

  def game_show_message(text: String) = {
    Document.msg.textContent = text
    Document.msg.style.display = "block"
    clearTimeout(game_message_timeout)
    game_message_timeout = setTimeout(2000) { Document.msg.style.display = "none" }
  }

  def title_show_message(msg: String, sub: String = "") = {
    Document.ts.innerHTML = "<h1>" + msg + "</h1>" + sub
    Document.ts.style.display = "block"
  }

  def game_run(time_now: Double) : Unit = {

    world.set_time_now(time_now)

    render.prepare_frame(0.1, 0.2, 0.5)

    // Update and render entities

    //note: entity.update() might add more game_entities
    for (entity <- world.entities.clone()) {
      if (!entity.dead) {
        entity.update()
      }
    }

    val alive_entities = new ArrayBuffer[Entity](0)
    for (entity <- world.entities) {
      if (!entity.dead) {
        alive_entities.addOne(entity)
      }
    }

    if(world.entities.length != alive_entities.length) {
      println("game_entities = " + world.entities.length + "  alive_entities=" + alive_entities.length)
    }

    world.entities = alive_entities


    map.draw()
    render.end_frame()
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
        Document.h.textContent = ""
        Document.a.textContent = ""
        world.player.dead = true
        // Set camera position for end screen
        render.camera = vec3(1856, 784, 2272)
        render.camera_yaw = 0
        render.camera_pitch = 0.5
      } else {
        game_init(game_map_index)
      }
    }
  }

}
