package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{vec3, vec3_rotate_y}
import com.publicscript.qucore.Model.model_load_container_async
import com.publicscript.qucore.Resources.{init_models, init_sfx, model_q}
import com.publicscript.qucore.TTT.ttt
import com.publicscript.qucore.Textures.texture_data
import org.scalajs.dom.MouseEvent


import org.scalajs.dom.window.requestAnimationFrame



object Game {

  def game_load() = {

    // Create textures
    ttt(texture_data).map(Game.world.display.render.create_texture)

    // Load map & model containers
    import scala.concurrent.ExecutionContext.Implicits.global
    println("loading js/target/scala-2.13/classes/build/levels")
    Game.world.map.load_container_async("js/target/scala-2.13/classes/build/levels").onComplete {
      result => {
        println("loaded Resources.map_data")
        Resources.map_data = result.get
        game_load_part2()
      }
    }

    println("loading js/target/scala-2.13/classes/build/models")
    model_load_container_async("js/target/scala-2.13/classes/build/models").onComplete {
      result => {
        println("loaded Resources.model_data")
        Resources.model_geometry = result.get
        init_models()
        game_load_part2()
      }
    }

  }
  def game_load_part2() = {

    if (Resources.map_data != null && Resources.model_geometry != null ) {

      println("game_load_part2")

      init_models()
      Game.world.display.render.submit_buffer()

      println("requestAnimationFrame(intro_frame)")

      var looper = new FrameLooper(intro_frame)
      //requestAnimationFrame(intro_frame)

      println("set up handlers")

      Display.f.onclick = (e: MouseEvent) => Display.g.requestFullscreen()
      Display.g.onclick = (e: MouseEvent) => {
        Display.g.onclick = (e: MouseEvent) => Display.c.requestPointerLock()

        init_sfx()
        game_init()

        looper.term()
        looper = new FrameLooper(game_run)
      }

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

 //   world.syncState()

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
