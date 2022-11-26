package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{vec3, vec3_rotate_y}
import com.publicscript.qucore.Model.model_load_container_async
import com.publicscript.qucore.Resources.{init_models, init_sfx, init_textures, model_q}

import org.scalajs.dom.MouseEvent
import org.scalajs.dom.window.requestAnimationFrame


object Game {

  def game_load() = {

    Resources.async_init(Game.game_load_complete)

  }

  def game_load_complete() = {

    Game.world.display.render.submit_buffer()

    var looper = new FrameLooper(intro_frame)

    Display.f.onclick = (e: MouseEvent) => Display.g.requestFullscreen()
    Display.g.onclick = (e: MouseEvent) => {
      Display.g.onclick = (e: MouseEvent) => Display.c.requestPointerLock()

      game_init()

      looper.term()
      looper = new FrameLooper(game_run)
    }

  }



  type FrameFunc = Double => Unit

  class FrameLooper (var drawFrame : FrameFunc, var loops: Int = -1) {

    requestAnimationFrame(loopFunc _)

    private def loopFunc(timenow: Double) : Unit = {
      if ( drawFrame != null && (loops > 0 || loops == -1)) {
        if ( loops >  0)
          loops = loops - 1
        drawFrame(timenow)
        requestAnimationFrame(loopFunc _)
      }
    }

    def term() = {
      drawFrame = null
    }
  }

  def intro_frame (time_now:Double) : Unit = {
    Game.world.display.render.prepare_frame(0,0,0)

    Game.world.display.render.draw(
      vec3(0,0,0), 0, 0, 1,
      model_q.frames(0), model_q.frames(0), 0,
      model_q.num_verts
    )
    Game.world.display.render.push_light(
      vec3(Math.sin(time_now*0.00033)*200, 100, -100),
      10, 255,192,32
    )
    Game.world.display.render.push_light(
      vec3_rotate_y(vec3(0, 0, 100),time_now*0.00063),
      10, 32,64,255
    )
    Game.world.display.render.push_light(
      vec3_rotate_y(vec3(100, 0, 0),time_now*0.00053),
      10, 196,128,255
    )

    Game.world.display.render.end_frame()
  }




  var world = new GameWorld()

  def game_init() = {
    world.init_level(0)
  }


  def game_run(time_now: Double) : Unit = {

    world.clock.set_time(time_now)

    world.syncState()

    world.display.render.prepare_frame(0.1, 0.2, 0.5)

    // Update and render entities
    world.entities = world.entities.filter((e:Entity)=> !e.dead)
    world.entities.clone().foreach((e:Entity)=> e.update())

    world.map.draw()
    world.display.render.end_frame()


    // Reset mouse movement and buttons that should be pressed, not held.
    world.player.input.mouse_x = 0
    world.player.input.mouse_y = 0
    world.player.input.key_next = false
    world.player.input.key_prev = false

    if (world.jump_to_next_level) {
      world.jump_to_next_level = false

      if (world.map_index == 1) {
        world.display.set_title_message("THE END", "THANKS FOR PLAYING ❤")
        world.display.set_health("")
        world.display.set_ammo("")

        world.player.dead = true

        // Set camera position for end screen
        world.display.render.camera = vec3(1856, 784, 2272)
        world.display.render.camera_yaw = 0
        world.display.render.camera_pitch = 0.5
      } else {
        world.map_index = world.map_index + 1
        world.init_level(world.map_index)

      }
    }

    if ( world.clock.frames % 30 == 0) {
      world.display.set_fps( world.clock.fps() )
    }
  }

}
