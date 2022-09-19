package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{vec3}


object Game {


  var world = new GameWorld()

  def game_init() = {
    world.init_level(0)
  }


  def game_run(time_now: Double) : Unit = {

    world.clock.set_time_now(time_now)

 //   world.syncState()

    world.render.prepare_frame(0.1, 0.2, 0.5)

    // Update and render entities
    world.entities = world.entities.filter((e:Entity)=> !e.dead)
    world.entities.clone().foreach((e:Entity)=> e.update())

    world.map.draw()
    world.render.end_frame()


    // Reset mouse movement and buttons that should be pressed, not held.
    world.player.input.mouse_x = 0
    world.player.input.mouse_y = 0
    world.player.input.key_next = false
    world.player.input.key_prev = false

    if (world.jump_to_next_level) {
      world.jump_to_next_level = false

      if (world.map_index == 1) {
        world.show_title_message("THE END", "THANKS FOR PLAYING ❤")
        world.show_health("")
        world.show_ammo("")

        world.player.dead = true

        // Set camera position for end screen
        world.render.camera = vec3(1856, 784, 2272)
        world.render.camera_yaw = 0
        world.render.camera_pitch = 0.5
      } else {
        world.map_index = world.map_index + 1
        world.init_level(world.map_index)

      }
    }

    if ( world.clock.frames % 30 == 0) {
      Document.fps.textContent = "FPS: " + world.clock.fps()
    }
  }

}
