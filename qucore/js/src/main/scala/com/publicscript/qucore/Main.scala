package com.publicscript.qucore

import com.publicscript.qucore.Audio.{Instrument, Track}
import org.scalajs.dom.{MouseEvent, document}
import org.scalajs.dom.html.Button

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
 //   val lib = new MyLibrary
 //   println(lib.sq(2))

    println(s"Using Scala.js version ${System.getProperty("java.vm.version")}")

    println("Let's yodel....")

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
}
