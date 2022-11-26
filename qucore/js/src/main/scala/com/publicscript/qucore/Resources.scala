package com.publicscript.qucore

import com.publicscript.qucore.Audio.Instrument
import com.publicscript.qucore.Map.MapData
import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Model.{RmfModel, model_load_container_async}
import com.publicscript.qucore.TTT.ttt
import org.scalajs.dom.AudioBuffer

import scala.collection.mutable.ArrayBuffer

object Resources {

  var map_data: Array[MapData] = null
  var model_geometry: Array[RmfModel] = null

  var model_q: Model.ModelRender = null

  // Particles
  var model_explosion: Model.ModelRender = null
  var model_blood: Model.ModelRender = null
  var model_gib: Model.ModelRender = null
  var model_gib_pieces: ArrayBuffer[Model.ModelRender] = new ArrayBuffer[Model.ModelRender](0)

  // Enemies
  var model_grunt: Model.ModelRender = null
  var model_enforcer: Model.ModelRender = null
  var model_ogre: Model.ModelRender = null
  var model_zombie: Model.ModelRender = null
  var model_hound: Model.ModelRender = null

  // Map Objects
  var model_barrel: Model.ModelRender = null
  var model_torch: Model.ModelRender = null

  // Weapon view models
  var model_shotgun: Model.ModelRender = null
  var model_nailgun: Model.ModelRender = null
  var model_grenadelauncher: Model.ModelRender = null

  // Pickups
  var model_pickup_nailgun: Model.ModelRender = null
  var model_pickup_grenadelauncher: Model.ModelRender = null
  var model_pickup_box: Model.ModelRender = null
  var model_pickup_grenades: Model.ModelRender = null
  var model_pickup_key: Model.ModelRender = null
  var model_door: Model.ModelRender = null

  // Projectiles
  var model_grenade: Model.ModelRender = null
  var model_plasma: Model.ModelRender = null // aka. nail
  var model_nail = model_plasma

  // Sounds
  var sfx_enemy_hit: AudioBuffer = null
  var sfx_enemy_gib: AudioBuffer = null
  var sfx_enemy_hound_attack: AudioBuffer = null

  var sfx_no_ammo: AudioBuffer = null
  var sfx_hurt: AudioBuffer = null
  var sfx_pickup: AudioBuffer = null

  var sfx_plasma_shoot: AudioBuffer = null

  var sfx_shotgun_shoot: AudioBuffer = null
  var sfx_shotgun_reload: AudioBuffer = null

  var sfx_nailgun_shoot: AudioBuffer = null
  var sfx_nailgun_hit: AudioBuffer = null

  var sfx_grenade_shoot: AudioBuffer = null
  var sfx_grenade_bounce: AudioBuffer = null
  var sfx_grenade_explode: AudioBuffer = null

  private def init_models(): Unit = {
    if (Resources.model_geometry == null) {
      throw new Exception("Why call init_models() if model_data is null?!?")
    }

    if (model_q != null) {
      println("Already ran init_models() ?!?")
      return
    }

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

    model_q = Game.world.model.model_init(model_geometry(3))
    model_explosion = Game.world.model.model_init(model_geometry(0), 0.1, 0.1, 0.1)
    model_blood = Game.world.model.model_init(model_geometry(0), 0.1, 0.2, 0.1)
    model_gib = Game.world.model.model_init(model_geometry(0), 0.3, 0.6, 0.3)
    model_grunt = Game.world.model.model_init(model_geometry(1), 2.5, 2.2, 2.5)
    model_enforcer = Game.world.model.model_init(model_geometry(1), 3, 2.7, 3)
    model_zombie = Game.world.model.model_init(model_geometry(1), 1.5, 2, 1.5)
    model_ogre = Game.world.model.model_init(model_geometry(1), 4, 3, 4)
    model_hound = Game.world.model.model_init(model_geometry(4), 2.5, 2.5, 2.5)

    model_barrel = Game.world.model.model_init(model_geometry(2), 2, 2, 2)
    model_torch = Game.world.model.model_init(model_geometry(7), 0.6, 1, 0.6)

    model_pickup_nailgun = Game.world.model.model_init(model_geometry(6), 1, 1, 1)
    model_pickup_grenadelauncher = Game.world.model.model_init(model_geometry(2), 1, 0.5, 0.5)
    model_pickup_box = Game.world.model.model_init(model_geometry(5), 0.7, 0.7, 0.7)
    model_pickup_grenades = Game.world.model.model_init(model_geometry(5), 0.5, 1, 0.5)
    model_pickup_key = Game.world.model.model_init(model_geometry(5), 0.1, 0.7, 0.1)

    model_door = Game.world.model.model_init(model_geometry(5), 5, 5, 0.5)

    model_shotgun = Game.world.model.model_init(model_geometry(2), 1, 0.2, 0.2)
    model_grenadelauncher = Game.world.model.model_init(model_geometry(2), 0.7, 0.4, 0.4)
    model_nailgun = Game.world.model.model_init(model_geometry(6), 0.7, 0.7, 0.7)

    model_grenade = Game.world.model.model_init(model_geometry(2), 0.3, 0.3, 0.3)
    model_nail = Game.world.model.model_init(model_geometry(2), 0.5, 0.1, 0.1)

    // Take some parts from the grunt model and build individual giblet models
    // from it. Arms and legs and stuff...
    for (i <- 0 until 204 by 34) {
      val m = Game.world.model.model_init(model_geometry(1), 2, 1, 2)
      m.frames(0) += i
      m.num_verts = 34
      model_gib_pieces.addOne(m)
    }

  }


  private def init_sfx(): Unit = {

    if (sfx_enemy_hit != null) {
      println("already ran init_sfx() ?!?")
      return
    }

    Game.world.audio.audio_init()


    // Generate sounds
    sfx_enemy_hit = Game.world.audio.create_sound(135, Instrument(8, 0, 0, true, 148, 1, 3, 5, 0, false, 139, 1, 0, 2653, 0, 2193, 255, 2, 639, 119, 2, 23, 0, 0, false, false, 0, 0, 0))
    sfx_enemy_gib = Game.world.audio.create_sound(140, Instrument(7, 0, 0, true, 148, 1, 7, 5, 0, true, 139, 1, 0, 4611, 789, 15986, 195, 2, 849, 119, 3, 60, 0, 0, false, true, 10, 176, 1))
    sfx_enemy_hound_attack = Game.world.audio.create_sound(132, Instrument(8, 0, 0, true, 192, 1, 8, 0, 0, true, 120, 1, 0, 5614, 0, 20400, 192, 1, 329, 252, 1, 55, 0, 0, true, true, 8, 192, 3))

    sfx_no_ammo = Game.world.audio.create_sound(120, Instrument(8, 0, 0, false, 96, 1, 8, 0, 0, false, 0, 0, 255, 0, 0, 1075, 232, 1, 2132, 255, 0, 0, 0, 0, false, false, 0, 0, 0))
    sfx_hurt = Game.world.audio.create_sound(135, Instrument(7, 3, 140, true, 232, 3, 8, 0, 9, true, 139, 3, 0, 4611, 1403, 34215, 256, 4, 1316, 255, 0, 0, 0, 1, false, true, 7, 255, 0))
    sfx_pickup = Game.world.audio.create_sound(140, Instrument(7, 0, 0, true, 187, 3, 8, 0, 0, true, 204, 3, 0, 4298, 927, 1403, 255, 0, 0, 0, 3, 35, 0, 0, false, false, 0, 0, 0))

    sfx_plasma_shoot = Game.world.audio.create_sound(135, Instrument(8, 0, 0, true, 147, 1, 6, 0, 0, true, 159, 1, 0, 197, 1234, 21759, 232, 2, 2902, 255, 2, 53, 0, 0, false, false, 0, 0, 0))

    sfx_shotgun_shoot = Game.world.audio.create_sound(135, Instrument(7, 3, 0, true, 255, 1, 6, 0, 0, true, 255, 1, 112, 548, 1979, 11601, 255, 2, 2902, 176, 2, 77, 0, 0, true, false, 10, 255, 1))
    sfx_shotgun_reload = Game.world.audio.create_sound(125, Instrument(9, 0, 0, true, 131, 1, 0, 0, 0, false, 0, 3, 255, 137, 22, 1776, 255, 2, 4498, 176, 2, 36, 2, 84, false, false, 3, 96, 0))

    sfx_nailgun_shoot = Game.world.audio.create_sound(130, Instrument(7, 0, 0, true, 132, 1, 8, 4, 0, true, 132, 2, 162, 0, 0, 8339, 232, 2, 2844, 195, 2, 40, 0, 0, false, false, 0, 0, 0))
    sfx_nailgun_hit = Game.world.audio.create_sound(135, Instrument(8, 0, 0, true, 148, 1, 0, 0, 0, false, 0, 1, 255, 0, 0, 2193, 128, 2, 6982, 119, 2, 23, 0, 0, false, false, 0, 0, 0))

    sfx_grenade_shoot = Game.world.audio.create_sound(127, Instrument(8, 0, 0, true, 171, 1, 9, 3, 0, true, 84, 3, 96, 2653, 0, 13163, 159, 2, 3206, 255, 2, 64, 0, 0, false, true, 9, 226, 0))
    sfx_grenade_bounce = Game.world.audio.create_sound(168, Instrument(7, 0, 124, false, 128, 0, 8, 5, 127, false, 128, 0, 125, 88, 0, 2193, 125, 1, 1238, 240, 1, 91, 3, 47, false, false, 0, 0, 0))
    sfx_grenade_explode = Game.world.audio.create_sound(135, Instrument(8, 0, 0, true, 195, 1, 6, 0, 0, true, 127, 1, 255, 197, 1234, 21759, 232, 2, 1052, 255, 4, 73, 3, 25, true, false, 10, 227, 1))

    Game.world.audio.play(Game.world.audio.create_song(Music.row_len, Music.pattern_len, Music.song_len, Music.music_data), 1, true)
  }



  def new_entity(world:World, entity_name:String, pos:Vec3, data1:Any, data2:Any):Entity = {
    // Entity Id to class - must be consistent with map_packer.c line ~900
    entity_name match {
      case "player" => new EntityPlayer(world, pos, data1, data2, new InputLocal())
      case "grunt" => new EntityEnemyGrunt(world, pos, data1.asInstanceOf[Double])

      case "enforcer" => new EntityEnemyGrunt(world, pos, data1.asInstanceOf[Double])
      case "ogre" => new EntityEnemyGrunt(world, pos, data1.asInstanceOf[Double])
      case "zombie" => new EntityEnemyZombie(world, pos, data1.asInstanceOf[Double])
      case "hound" => new EntityEnemyHound(world, pos, data1.asInstanceOf[Double])
      case "nailgun" => new EntityPickupNailgun(world, pos)
      case "grenadelauncher" => new EntityPickupGrenadeLauncher(world, pos)
      case "health" => new EntityPickupHealth(world, pos)
      case "nails" => new EntityPickupNails(world, pos)
      case "grenades" => new EntityPickupGrenades(world, pos)
      case "barrel" => new EntityBarrel(world, pos)
      case "light" => new EntityLight(world, pos, data1.asInstanceOf[Double],data2.asInstanceOf[Int])
      case "trigger_level" => new EntityTriggerLevel(world, pos)
      case "door" => new EntityDoor(world, pos, data1.asInstanceOf[Int], data2.asInstanceOf[Double])
      case "pickup_key" => new EntityPickupKey(world, pos)
      case "torch" => new EntityTorch(world, pos)

      case "gib" => new EntityProjectileGib(world, pos)
      case "grenade" => new EntityProjectileGrenade(world, pos)
      case "nail" => new EntityProjectileNail(world, pos)
      case "plasma" => new EntityProjectilePlasma(world, pos)
      case "shell" => new EntityProjectileShell(world, pos)

      case "particle" => new EntityParticle(world, pos)

      case _ => throw new IllegalArgumentException(s"Unknown entity name: $entity_name")
    }
  }


  private val texture_data:Array[Array[Any]] = Array(
    Array(64,64,0,2,3,1.4,2,17176,1.3),
    Array(64,64,38751,1,18,4,2,2,27,9,65530,0,7,1,-1,9,13,5,52,8,8,65528,39039,4,0,0,0,64,512,15,4,0,0,0,64,64,14),
    Array(64,64,38751,1,18,4,2,2,27,9,65530,0,7,1,-1,0,13,64,52,64,8,65531,39039,4,0,0,0,64,512,15,4,0,0,0,64,64,14),
    Array(64,64,13119,4,1,0,0,64,64,15,0,24,11,17,50,65523,2,8),
    Array(32,32,21839,1,0,2,10,2,11,4,65528,10,25931,4,0,0,0,32,32,14),
    Array(32,32,17487,0,1,1,30,30,65528,11,21580,4,0,0,0,32,32,15),
    Array(32,32,30015,4,5,0,0,32,32,15,1,5,4,2,2,22,6,65522,0,8),
    Array(32,32,8751,1,1,1,8,4,11,5,65524,15,17487,4,0,0,0,64,64,15),
    Array(32,32,13119,4,4,0,0,32,32,15,1,10,3,11,6,25,10,64536,64568,65519),
    Array(32,32,8751,1,1,1,3,3,4,4,65524,14,21565,1,1,-1,15,1,16,16,65522,7,0,1,-1,0,1,15,16,6,65521,0,4,4,0,0,0,32,32,15),
    Array(32,32,8719,2,63506,1,4,0,0,0,32,32,12),
    Array(32,32,21295,4,10,0,-4,32,298,10,2,4372,1),
    Array(32,32,8463,1,-1,1,35,1,35,4,65522,10,34399,0,-1,6,34,6,65526,2,34399,2,29479,1,4,6,0,0,32,32,5),
    Array(32,32,5535,4,0,0,0,128,64,14),
    Array(32,32,8463,1,0,0,3,3,4,4,0,10,65521,0,4,4,23,23,10,64885,21551,3,4,16,13,0,11,"::][::",4,0,4,4,26,26,15),
    Array(32,32,8751,1,1,1,8,3,11,5,65524,15,17487,0,9,6,14,13,15,65525,4383,4,0,0,0,64,64,12,3,10,11,20267,0,8,"---"),
    Array(32,32,17487,4,5,0,0,32,32,15,1,4,4,3,3,22,22,65523,7,30587),
    Array(64,64,38767,2,36875,2.5,1,4,10,15,8,39,59,15,15,8463,1,3,30,14,5,15,6,12813,4367,38671,0,20,1,22,6,13119,10,38671,4,0,0,0,64,64,11),
    Array(32,32,40975,2,63308,1.5,2,63751,7.3),
    Array(64,64,13119,4,17,0,0,64,64,15,0,0,29,64,64,0,0,89,4,4,21,-6,22,24,15),
    Array(32,32,13119,4,9,0,0,32,32,15,4,8,6,-22,21,32,15,4,18,0,0,32,32,4),
    Array(64,64,13119,0,0,0,64,64,0,0,64271,3,-1,50,33795,0,32,"XXX",4,7,0,0,64,64,6),
    Array(64,64,34063,4,7,0,0,64,64,12,2,12554,1),
    Array(32,32,65535,4,12,0,0,32,32,9,3,6,30,61455,0,25,"+"),
    Array(32,32,5903,4,12,0,0,32,32,9,3,5,14,65529,0,12,"NIИ"),
    Array(32,32,64271,0,12,1,7,30,65528,8,63247,4,7,0,0,32,32,8),
    Array(32,32,13119,1,1,1,14,14,16,32,56328,15,26399,1,-7,17,14,14,16,32,56328,8,26159,2,29706,1,4,0,0,0,32,320,14),
    Array(32,32,33567,1,1,1,6,30,16,31,65526,15,33823,1,9,-14,6,30,16,32,65526,15,29743,2,55625,1.5,4,0,0,0,32,320,15),
    Array(32,32,12559,1,1,1,14,14,16,16,65525,7,21295,0,1,1,14,14,65525,0,34399,0,17,17,14,14,65524,0,34399,2,8,1.5),
    Array(32,32,9503,4,11,0,0,32,32,12,1,1,1,6,7,6,8,65521,0,4),
    Array(32,32,15,4,18,0,16,32,32,15,4,27,0,-16,32,32,10))


  private def init_textures() = {
    // Create textures
    ttt(texture_data).map(Game.world.display.render.create_texture)

  }

  object Music {

    import com.publicscript.qucore.Audio.{Instrument, Track}

    private val track1 = new Track(new Instrument(7,0,0,true,255,0,7,0,0,true,255,0,0,100,0,3636,254,2,1199,254,4,71,0,0,false,false,0,0,0),Array(1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1),Array(Array(126,126,0,0,126,0,0,0,0,0,0,0,0,0,0,0,126,126,0,0,126,0,0,0,0,0,0,0,0,0,0,0)))
    private val track2 = new Track(new Instrument(6,0,0,false,255,2,6,0,18,false,255,2,0,100000,56363,100000,199,2,200,254,8,24,0,0,false,false,0,0,0),Array(0,0,2,2,3,4,2,2,3,5,2,2,3,4,2,2,3,5),Array(Array(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(132,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(133,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,128,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(125,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(120,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)))
    private val track3 = new Track(new Instrument(7,0,0,false,87,2,8,0,0,false,16,3,8,0,22,2193,255,3,1162,51,10,182,2,190,false,true,10,96,0),Array(0,0,0,0,0,0,1,1,1,1,1,1,1,1),Array(Array(149,149,0,0,149,0,149,0,149,149,0,0,149,0,149,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)))
    private val track4 = new Track(new Instrument(8,0,0,false,65,2,6,0,0,false,243,3,0,200,7505,20000,204,4,6180,81,4,198,0,0,false,false,6,131,0),Array(0,0,0,0,0,0,0,0,0,0,1,1,2,3,1,1,2,3),Array(Array(132,0,0,0,0,0,0,0,133,0,0,0,137,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(132,0,0,0,0,0,0,0,133,0,0,0,130,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(132,0,0,0,0,0,0,0,133,0,0,0,125,0,0,0,0,0,0,0,125,0,0,0,0,0,0,0,0,0,0,0)))

    val music_data : Array[Track] = Array(track1,track2,track3,track4)

    val row_len=6014
    val pattern_len=21
    val song_len=88
  }

  def async_init(onComplete:() => Unit) = {

    init_textures()

    // Load map & model containers
    import scala.concurrent.ExecutionContext.Implicits.global
    println("loading js/target/scala-2.13/classes/build/levels")
    Game.world.map.load_container_async("js/target/scala-2.13/classes/build/levels").onComplete {
      result => {
        println("loaded Resources.map_data")
        Resources.map_data = result.get
        if (Resources.map_data != null && Resources.model_geometry != null ) {
          init_models()
          onComplete()
        }
      }
    }

    println("loading js/target/scala-2.13/classes/build/models")
    model_load_container_async("js/target/scala-2.13/classes/build/models").onComplete {
      result => {
        println("loaded Resources.model_data")
        Resources.model_geometry = result.get
        if (Resources.map_data != null && Resources.model_geometry != null ) {
          init_models()
          onComplete()
        }
      }
    }

    init_sfx()
  }

}
