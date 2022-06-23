package com.publicscript.qucore

import com.publicscript.qucore.Audio.{Instrument, Track}
import com.publicscript.qucore.Map.{ map_load_container_async}
import com.publicscript.qucore.Render.{r_create_texture, r_init}
import com.publicscript.qucore.Resources.map_data
import com.publicscript.qucore.TTT.ttt
import com.publicscript.qucore.Textures.texture_data
import org.scalajs.dom.{MouseEvent, document}
import org.scalajs.dom.html.Button

import com.publicscript.qucore.Render
import org.scalajs.dom.window.requestAnimationFrame

object Main {

  def getButton(buttonId: String): Option[Button] = {
    val queryResult = document.querySelector(s"#$buttonId")
    queryResult match {
      case button: Button => Some(button)
      case other =>
        println(s"Element with ID $buttonId is not an image, it's $other")
        None
    }
  }


  def main(args: Array[String]): Unit = {


    import scala.scalajs.js.timers._

    setTimeout(1000) {
      // work
      println("Work!")
    }

    setTimeout(1100) {
      // work
      println("Input.input_init()")

      Input.input_init()
    }

    setTimeout(1200) {
      // work
      println("Render.r_init()")

      Render.r_init()
    }

    setTimeout(1300) {
      // work
      println("Resources.game_load()")
      Resources.game_load()

      println("Game.game_init(0)")
      Game.game_init(0)
    }

    setTimeout(1500) {
      // work
      println("display something")

      import MathUtils.vec3
      import MathUtils.vec3_rotate_y

      def run_frame(time_now:Double):Unit = {
        Render.r_prepare_frame(0, 12, 0)
        Render.r_draw(vec3(0, 0, 0), 0, 0, 1, Resources.model_q.f(0), Resources.model_q.f(0), 0, Resources.model_q.nv)
        Render.r_push_light(vec3(Math.sin(time_now * 0.00033) * 200, 100, -100), 10, 255, 192, 32)
        Render.r_push_light(vec3_rotate_y(vec3(0, 0, 100), time_now * 0.00063), 10, 32, 64, 255)
        Render.r_push_light(vec3_rotate_y(vec3(100, 0, 0), time_now * 0.00053), 10, 196, 128, 255)
        Render.r_end_frame()
      }
      requestAnimationFrame(run_frame)
    }


  }
/*
  def main(args: Array[String]): Unit = {

    var map_data = _
    var model_data = Array.empty[Unit]
    var model_explosion = _
    var model_blood = _
    var model_gib = _
    val model_gib_pieces = Array.empty[Unit]
    var model_grunt = _
    var model_enforcer = _
    var model_ogre = _
    var model_zombie = _
    var model_hound = _
    var model_barrel = _
    var model_torch = _
    var model_shotgun = _
    var model_nailgun = _
    var model_grenadelauncher = _
    var model_pickup_nailgun = _
    var model_pickup_grenadelauncher = _
    var model_pickup_box = _
    var model_pickup_grenades = _
    var model_pickup_key = _
    var model_door = _
    var model_grenade = _
    var model_plasma = _
    var sfx_enemy_hit = _
    var sfx_enemy_gib = _
    var sfx_enemy_hound_attack = _
    var sfx_no_ammo = _
    var sfx_hurt = _
    var sfx_pickup = _
    var sfx_plasma_shoot = _
    var sfx_shotgun_shoot = _
    var sfx_shotgun_reload = _
    var sfx_nailgun_shoot = _
    var sfx_nailgun_hit = _
    var sfx_grenade_shoot = _
    var sfx_grenade_bounce = _
    var sfx_grenade_explode = _
    val game_load = /* Unsupported: AsyncArrowFunctionExpression */ async () => {
      r_init();

      // Create textures
      ttt(texture_data).map(r_create_texture);

      // Load map & model containers
      map_data = await map_load_container(/*DEBUG[*/ 'build/' + /*]*/ 'l');
      model_data = await model_load_container(/*DEBUG[*/ 'build/' + /*]*/ 'm');

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

      model_q = model_init(model_data[3]);

      model_explosion = model_init(model_data[0], 0.1,0.1,0.1);
      model_blood = model_init(model_data[0], 0.1,0.2,0.1);
      model_gib = model_init(model_data[0], 0.3,0.6,0.3);

      model_grunt = model_init(model_data[1], 2.5,2.2,2.5);
      model_enforcer = model_init(model_data[1], 3,2.7,3);
      model_zombie = model_init(model_data[1], 1.5,2,1.5);
      model_ogre = model_init(model_data[1], 4,3,4);
      model_hound = model_init(model_data[4],2.5,2.5,2.5);

      model_barrel = model_init(model_data[2], 2, 2, 2);
      model_torch = model_init(model_data[7], 0.6,1,0.6);

      model_pickup_nailgun = model_init(model_data[6], 1, 1, 1);
      model_pickup_grenadelauncher = model_init(model_data[2], 1, 0.5, 0.5);
      model_pickup_box = model_init(model_data[5], 0.7, 0.7, 0.7);
      model_pickup_grenades = model_init(model_data[5], 0.5, 1, 0.5);
      model_pickup_key = model_init(model_data[5], 0.1, 0.7, 0.1);

      model_door = model_init(model_data[5], 5, 5, 0.5);

      model_shotgun = model_init(model_data[2], 1,0.2,0.2);
      model_grenadelauncher = model_init(model_data[2], 0.7,0.4,0.4);
      model_nailgun = model_init(model_data[6], 0.7,0.7,0.7);

      model_grenade = model_init(model_data[2], 0.3,0.3,0.3);
      model_nail = model_init(model_data[2], 0.5,0.1,0.1);

      // Take some parts from the grunt model and build individual giblet models
      // from it. Arms and legs and stuff...
      for (let i = 0; i < 204; i+=34) {
        let m = model_init(model_data[1], 2,1,2);
        m.f[0] += i;
        m.nv = 34;
        model_gib_pieces.push(m);
      }


      r_submit_buffer();
      requestAnimationFrame(run_frame);

      f.onclick = () => g.requestFullscreen();
      g.onclick = () => {
        g.onclick = () => c.requestPointerLock();
        g.onclick();

        audio_init();

        // Generate sounds
        sfx_enemy_hit = audio_create_sound(135, [8,0,0,1,148,1,3,5,0,0,139,1,0,2653,0,2193,255,2,639,119,2,23,0,0,0,0,0,0,0]);
        sfx_enemy_gib = audio_create_sound(140, [7,0,0,1,148,1,7,5,0,1,139,1,0,4611,789,15986,195,2,849,119,3,60,0,0,0,1,10,176,1]);
        sfx_enemy_hound_attack = audio_create_sound(132, [8,0,0,1,192,1,8,0,0,1,120,1,0,5614,0,20400,192,1,329,252,1,55,0,0,1,1,8,192,3]);

        sfx_no_ammo = audio_create_sound(120, [8,0,0,0,96,1,8,0,0,0,0,0,255,0,0,1075,232,1,2132,255,0,0,0,0,0,0,0,0,0]);
        sfx_hurt = audio_create_sound(135, [7,3,140,1,232,3,8,0,9,1,139,3,0,4611,1403,34215,256,4,1316,255,0,0,0,1,0,1,7,255,0]);
        sfx_pickup = audio_create_sound(140, [7,0,0,1,187,3,8,0,0,1,204,3,0,4298,927,1403,255,0,0,0,3,35,0,0,0,0,0,0,0]);

        sfx_plasma_shoot = audio_create_sound(135, [8,0,0,1,147,1,6,0,0,1,159,1,0,197,1234,21759,232,2,2902,255,2,53,0,0,0,0,0,0,0]);

        sfx_shotgun_shoot = audio_create_sound(135, [7,3,0,1,255,1,6,0,0,1,255,1,112,548,1979,11601,255,2,2902,176,2,77,0,0,1,0,10,255,1]);
        sfx_shotgun_reload = audio_create_sound(125, [9,0,0,1,131,1,0,0,0,0,0,3,255,137,22,1776,255,2,4498,176,2,36,2,84,0,0,3,96,0]);

        sfx_nailgun_shoot = audio_create_sound(130, [7,0,0,1,132,1,8,4,0,1,132,2,162,0,0,8339,232,2,2844,195,2,40,0,0,0,0,0,0,0]);
        sfx_nailgun_hit = audio_create_sound(135, [8,0,0,1,148,1,0,0,0,0,0,1,255,0,0,2193,128,2,6982,119,2,23,0,0,0,0,0,0,0]);

        sfx_grenade_shoot = audio_create_sound(127, [8,0,0,1,171,1,9,3,0,1,84,3,96,2653,0,13163,159,2,3206,255,2,64,0,0,0,1,9,226,0]);
        sfx_grenade_bounce = audio_create_sound(168, [7,0,124,0,128,0,8,5,127,0,128,0,125,88,0,2193,125,1,1238,240,1,91,3,47,0,0,0,0,0]);
        sfx_grenade_explode = audio_create_sound(135, [8,0,0,1,195,1,6,0,0,1,127,1,255,197,1234,21759,232,2,1052,255,4,73,3,25,1,0,10,227,1]);


        audio_play(audio_create_song(...music_data), 1, 1);
        game_init(0);
        run_frame = game_run;
      };
    }
    var run_frame = (time_now: Double) => {
      r_prepare_frame()
      r_draw(vec3(0, 0, 0), 0, 0, 1, model_q.f(0), model_q.f(0), 0, model_q.nv)
      r_push_light(vec3(Math.sin(time_now * 0.00033) * 200, 100, -100), 10, 255, 192, 32)
      r_push_light(vec3_rotate_y(vec3(0, 0, 100), time_now * 0.00063), 10, 32, 64, 255)
      r_push_light(vec3_rotate_y(vec3(100, 0, 0), time_now * 0.00053), 10, 196, 128, 255)
      r_end_frame()
      requestAnimationFrame(run_frame)
    }


  }
*/


/*

  def main(args: Array[String]): Unit = {
 //   val lib = new MyLibrary
 //   println(lib.sq(2))

    println(s"Using Scala.js version ${System.getProperty("java.vm.version")}")

    println("Let's yodel....")

    getButton("load_maps").get.onclick = (e:MouseEvent) => {

      r_init()

      // Create textures
      ttt(texture_data).map(r_create_texture)

      import scala.concurrent.ExecutionContext.Implicits.global
      println("before map_load_container_async")
      map_load_container_async("js/target/scala-2.13/classes/build/levels").onComplete {
        result => Resources.map_data = result.get
      }
      println("after map_load_container_async")

    //  Audio.audio_init()
    //  Audio.audio_play_async(Audio.audio_load_url_async("https://s3-us-west-2.amazonaws.com/s.cdpn.io/123941/Yodel_Sound_Effect.mp3"))
      }
    getButton("load_models").get.onclick = (e:MouseEvent) => {
      Audio.audio_init()
      Audio.audio_play_async(Audio.audio_load_url_async("https://s3-us-west-2.amazonaws.com/s.cdpn.io/123941/Yodel_Sound_Effect.mp3"))
    }




    getButton("yodel").get.onclick = (e:MouseEvent) => {
      Audio.audio_init()
      Audio.audio_play_async(Audio.audio_load_url_async("https://s3-us-west-2.amazonaws.com/s.cdpn.io/123941/Yodel_Sound_Effect.mp3"))
    }

    getButton("sfx_shotgun_shoot").get.onclick = (e:MouseEvent) => {
      Audio.audio_init()
      val sfx_shotgun_shoot = Audio.audio_create_sound(135,  new Instrument(7,3,0,true,255,1,6,0,0,true,255,1,112,548,1979,11601,255,2,2902,176,2,77,0,0,true,false,10,255,1))
      Audio.audio_play(sfx_shotgun_shoot)
    }

    getButton("sfx_nailgun_shoot").get.onclick = (e:MouseEvent) => {
      val sfx_nailgun_shoot = Audio.audio_create_sound(130, new Instrument(7,0,0,true,132,1,8,4,0,true,132,2,162,0,0,8339,232,2,2844,195,2,40,0,0,false,false,0,0,0))
      Audio.audio_play(sfx_nailgun_shoot)
    }

    getButton("sfx_plasma_shoot").get.onclick = (e:MouseEvent) => {

      val sfx_plasma_shoot = Audio.audio_create_sound(135, new Instrument(8,0,0,true,147,1,6,0,0,true,159,1,0,197,1234,21759,232,2,2902,255,2,53,0,0,false,false,0,0,0))
      val sfx_grenade_explode = Audio.audio_create_sound(135, new Instrument(8,0,0,true,195,1,6,0,0,true,127,1,255,197,1234,21759,232,2,1052,255,4,73,3,25,true,false,10,227,1))
      val sfx_grenade_bounce = Audio.audio_create_sound(168, new Instrument(7,0,124,false,128,0,8,5,127,false,128,0,125,88,0,2193,125,1,1238,240,1,91,3,47,false,false,0,0,0))

      Audio.audio_play(sfx_plasma_shoot)
    }


    getButton("sfx_grenade_bounce").get.onclick = (e:MouseEvent) => {

      //sfx_plasma_shoot = audio_create_sound(135, [8,0,0,1,147,1,6,0,0,1,159,1,0,197,1234,21759,232,2,2902,255,2,53,0,0,0,0,0,0,0]);
      val sfx_plasma_shoot = Audio.audio_create_sound(135, new Instrument(8,0,0,true,147,1,6,0,0,true,159,1,0,197,1234,21759,232,2,2902,255,2,53,0,0,false,false,0,0,0))
      val sfx_grenade_explode = Audio.audio_create_sound(135, new Instrument(8,0,0,true,195,1,6,0,0,true,127,1,255,197,1234,21759,232,2,1052,255,4,73,3,25,true,false,10,227,1))
      val sfx_grenade_bounce = Audio.audio_create_sound(168, new Instrument(7,0,124,false,128,0,8,5,127,false,128,0,125,88,0,2193,125,1,1238,240,1,91,3,47,false,false,0,0,0))


      Audio.audio_play(sfx_grenade_bounce)
    }
    getButton("sfx_grenade_explode").get.onclick = (e:MouseEvent) => {

      val sfx_plasma_shoot = Audio.audio_create_sound(135, new Instrument(8,0,0,true,147,1,6,0,0,true,159,1,0,197,1234,21759,232,2,2902,255,2,53,0,0,false,false,0,0,0))
      val sfx_grenade_explode = Audio.audio_create_sound(135, new Instrument(8,0,0,true,195,1,6,0,0,true,127,1,255,197,1234,21759,232,2,1052,255,4,73,3,25,true,false,10,227,1))
      val sfx_grenade_bounce = Audio.audio_create_sound(168, new Instrument(7,0,124,false,128,0,8,5,127,false,128,0,125,88,0,2193,125,1,1238,240,1,91,3,47,false,false,0,0,0))


      Audio.audio_play(sfx_grenade_explode)
    }

    getButton("song").get.onclick = (e:MouseEvent) => {

      Audio.audio_init()

      println("creating song...")
      val song = Audio.audio_create_song(Music.row_len, Music.pattern_len, Music.song_len, Music.music_data)
      println("now playing song...")
      Audio.audio_play(song, 1, true)

    }

    Input.input_init()
  }

*/



}
