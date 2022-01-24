package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{vec3, vec3_rotate_y}
import com.publicscript.qucore.Game.{game_init, game_run}
import com.publicscript.qucore.Render._
import com.publicscript.qucore.Map.MapData
import com.publicscript.qucore.Model.{RmfModel, model_init}
import com.publicscript.qucore.Audio._
import org.scalajs.dom.{AudioBuffer, MouseEvent}
import com.publicscript.qucore.Document._

import com.publicscript.qucore.Render.{r_init,r_create_texture}
import com.publicscript.qucore.TTT.ttt
import com.publicscript.qucore.Textures.texture_data
//import com.publicscript.qucore.Map.map_load_container
//import com.publicscript.qucore.Model.model_load_container
import org.scalajs.dom.window.requestAnimationFrame

import scala.collection.mutable.ArrayBuffer

object Resources {

  var map_data : Array[MapData] = null
  var model_data : Array[RmfModel] = null


  var model_q : Model.Model = null

  // Particles
  var model_explosion : Model.Model = null
  var model_blood : Model.Model = null
  var model_gib : Model.Model = null
  var model_gib_pieces : ArrayBuffer[Model.Model] = new ArrayBuffer[Model.Model](0)

  // Enemies
  var model_grunt : Model.Model = null
  var model_enforcer : Model.Model = null
  var model_ogre : Model.Model = null
  var model_zombie : Model.Model = null
  var model_hound : Model.Model = null

  // Map Objects
  var model_barrel : Model.Model = null
  var model_torch : Model.Model = null

  // Weapon view models
  var model_shotgun : Model.Model = null
  var model_nailgun : Model.Model = null
  var model_grenadelauncher : Model.Model = null

  // Pickups
  var model_pickup_nailgun : Model.Model = null
  var model_pickup_grenadelauncher : Model.Model = null
  var model_pickup_box : Model.Model = null
  var model_pickup_grenades : Model.Model = null
  var model_pickup_key : Model.Model = null
  var model_door : Model.Model = null

  // Projectiles
  var model_grenade : Model.Model = null
  var model_plasma : Model.Model = null // aka. nail
  var model_nail = model_plasma

  // Sounds
  var sfx_enemy_hit : AudioBuffer = null
  var sfx_enemy_gib : AudioBuffer = null
  var sfx_enemy_hound_attack : AudioBuffer = null

  var sfx_no_ammo : AudioBuffer = null
  var sfx_hurt : AudioBuffer = null
  var sfx_pickup : AudioBuffer = null

  var sfx_plasma_shoot : AudioBuffer = null

  var sfx_shotgun_shoot : AudioBuffer = null
  var sfx_shotgun_reload : AudioBuffer = null

  var sfx_nailgun_shoot : AudioBuffer = null
  var sfx_nailgun_hit : AudioBuffer = null

  var sfx_grenade_shoot : AudioBuffer = null
  var sfx_grenade_bounce : AudioBuffer = null
  var sfx_grenade_explode : AudioBuffer = null



  def game_load() = {

    r_init()

    // Create textures
    ttt(texture_data).map(r_create_texture)

    // Load map & model containers
//    map_data = map_load_container("classes/build/levels")
//    model_data = model_load_container("classes/build/model")

    // Create models. Many models share the same geometry just with different
    // sizes and textures.
    // 0: generic blob
    // 1: humanoid
    // 2: barrel
    // 3: q logo
    // 4: hound
    // 5: box
    // 6: nailgun
    // 7: torch

    model_q = model_init(model_data(3))

    model_explosion = model_init(model_data(0), 0.1,0.1,0.1)
    model_blood = model_init(model_data(0), 0.1,0.2,0.1)
    model_gib = model_init(model_data(0), 0.3,0.6,0.3)

    model_grunt = model_init(model_data(1), 2.5,2.2,2.5)
    model_enforcer = model_init(model_data(1), 3,2.7,3)
    model_zombie = model_init(model_data(1), 1.5,2,1.5)
    model_ogre = model_init(model_data(1), 4,3,4)
    model_hound = model_init(model_data(4),2.5,2.5,2.5)

    model_barrel = model_init(model_data(2), 2, 2, 2)
    model_torch = model_init(model_data(7), 0.6,1,0.6)

    model_pickup_nailgun = model_init(model_data(6), 1, 1, 1)
    model_pickup_grenadelauncher = model_init(model_data(2), 1, 0.5, 0.5)
    model_pickup_box = model_init(model_data(5), 0.7, 0.7, 0.7)
    model_pickup_grenades = model_init(model_data(5), 0.5, 1, 0.5)
    model_pickup_key = model_init(model_data(5), 0.1, 0.7, 0.1)

    model_door = model_init(model_data(5), 5, 5, 0.5)

    model_shotgun = model_init(model_data(2), 1,0.2,0.2)
    model_grenadelauncher = model_init(model_data(2), 0.7,0.4,0.4)
    model_nailgun = model_init(model_data(6), 0.7,0.7,0.7)

    model_grenade = model_init(model_data(2), 0.3,0.3,0.3)
    model_nail = model_init(model_data(2), 0.5,0.1,0.1)

    // Take some parts from the grunt model and build individual giblet models
    // from it. Arms and legs and stuff...
    for (i <- 0 until 204 by 34) {
      val m = model_init(model_data(1), 2,1,2)
      m.f(0) += i
      m.nv = 34
      model_gib_pieces.addOne(m)
    }


    r_submit_buffer()
    requestAnimationFrame(run_frame)

    f.onclick = (e:MouseEvent) => g.requestFullscreen()
    g.onclick = (e:MouseEvent) => {
      g.onclick = (e:MouseEvent) => c.requestPointerLock()
      // g.onclick();  <--Why????

      audio_init()

      // Generate sounds
      sfx_enemy_hit = audio_create_sound(135, Instrument(8,0,0,true,148,1,3,5,0,false,139,1,0,2653,0,2193,255,2,639,119,2,23,0,0,false,false,0,0,0))
      sfx_enemy_gib = audio_create_sound(140, Instrument(7,0,0,true,148,1,7,5,0,true,139,1,0,4611,789,15986,195,2,849,119,3,60,0,0,false,true,10,176,1))
      sfx_enemy_hound_attack = audio_create_sound(132, Instrument(8,0,0,true,192,1,8,0,0,true,120,1,0,5614,0,20400,192,1,329,252,1,55,0,0,true,true,8,192,3))

      sfx_no_ammo = audio_create_sound(120, Instrument(8,0,0,false,96,1,8,0,0,false,0,0,255,0,0,1075,232,1,2132,255,0,0,0,0,false,false,0,0,0))
      sfx_hurt = audio_create_sound(135, Instrument(7,3,140,true,232,3,8,0,9,true,139,3,0,4611,1403,34215,256,4,1316,255,0,0,0,1,false,true,7,255,0))
      sfx_pickup = audio_create_sound(140, Instrument(7,0,0,true,187,3,8,0,0,true,204,3,0,4298,927,1403,255,0,0,0,3,35,0,0,false,false,0,0,0))

      sfx_plasma_shoot = audio_create_sound(135, Instrument(8,0,0,true,147,1,6,0,0,true,159,1,0,197,1234,21759,232,2,2902,255,2,53,0,0,false,false,0,0,0))

      sfx_shotgun_shoot = audio_create_sound(135, Instrument(7,3,0,true,255,1,6,0,0,true,255,1,112,548,1979,11601,255,2,2902,176,2,77,0,0,true,false,10,255,1))
      sfx_shotgun_reload = audio_create_sound(125, Instrument(9,0,0,true,131,1,0,0,0,false,0,3,255,137,22,1776,255,2,4498,176,2,36,2,84,false,false,3,96,0))

      sfx_nailgun_shoot = audio_create_sound(130,  Instrument(7,0,0,true,132,1,8,4,0,true,132,2,162,0,0,8339,232,2,2844,195,2,40,0,0,false,false,0,0,0))
      sfx_nailgun_hit = audio_create_sound(135,  Instrument(8,0,0,true,148,1,0,0,0,false,0,1,255,0,0,2193,128,2,6982,119,2,23,0,0,false,false,0,0,0))

      sfx_grenade_shoot = audio_create_sound(127,  Instrument(8,0,0,true,171,1,9,3,0,true,84,3,96,2653,0,13163,159,2,3206,255,2,64,0,0,false,true,9,226,0))
      sfx_grenade_bounce = audio_create_sound(168,  Instrument(7,0,124,false,128,0,8,5,127,false,128,0,125,88,0,2193,125,1,1238,240,1,91,3,47,false,false,0,0,0))
      sfx_grenade_explode = audio_create_sound(135,  Instrument(8,0,0,true,195,1,6,0,0,true,127,1,255,197,1234,21759,232,2,1052,255,4,73,3,25,true,false,10,227,1))

      audio_play(audio_create_song(Music.row_len, Music.pattern_len, Music.song_len, Music.music_data),1,true)
      game_init(0)
   //   run_frame = game_run
    }
  }

  def run_frame (time_now:Double) : Unit = {
    r_prepare_frame(0,0,0)

    r_draw(
      vec3(0,0,0), 0, 0, 1,
      model_q.f(0), model_q.f(0), 0,
    model_q.nv
    )
    r_push_light(
      vec3(Math.sin(time_now*0.00033)*200, 100, -100),
      10, 255,192,32
    )
    r_push_light(
      vec3_rotate_y(vec3(0, 0, 100),time_now*0.00063),
      10, 32,64,255
    )
    r_push_light(
      vec3_rotate_y(vec3(100, 0, 0),time_now*0.00053),
      10, 196,128,255
    )

    r_end_frame()

    requestAnimationFrame(Game.game_run _)
  }

  game_load()

}
