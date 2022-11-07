package com.publicscript.qucore

import com.publicscript.qucore.MathUtils._
import com.publicscript.qucore.Resources.map_data
import org.scalajs.dom.AudioBuffer

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js.timers.{SetTimeoutHandle}
import scala.util.Random


class GameWorld extends World {

  private var message_timeout : SetTimeoutHandle = _
  var jump_to_next_level = false

  var entities = new ArrayBuffer[Entity](0)
  var player : EntityPlayer = _
  var map = new Map()
  var map_index : Int = _

  val audio = new Audio()
  val display = new Display()
  var model = new Model(display.render)


  def play_sound(sound: AudioBuffer, pos:Vec3 = null) = {
    if ( pos != null) {
      val distance_to_camera = display.render.get_distance_to_camera(pos)
      val angle_to_camera = display.render.get_angle_to_camera(pos)
      val volume = clamp(scale(distance_to_camera, 64, 1200, 1, 0), 0, 1)
      val pan = Math.sin(angle_to_camera) * -1
      audio.play(sound, volume, false, pan)
    } else {
      audio.play(sound)
    }
  }


  def get_entity_group(group_name:String) : Array[Entity] = {
    group_name match {
      case "none" => Array()
      case "enemy" => entities.filter((e:Entity) => e.is_enemy && !e.dead).toArray
      case "player" => entities.filter((e:Entity) => e.is_friend && !e.dead).toArray
      case _ => throw new IllegalArgumentException(s"Unknown group name: $group_name")
    }
  }


  private def new_entity(world:World, entity_name:String, pos:Vec3, data1:Any, data2:Any):Entity = {
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


  var clock = new Clock()
  def time():Double = {
    clock.time
  }
  def tick():Double = {
    clock.tick
  }

  var randomObj = new Random(123)

  def randomInit() = {
    randomObj = new Random(123)
  }
  def random():Double = {
    randomObj.nextDouble()
  }

  var seq:Int = 0
  def nextSeq():Int = {
    seq = seq + 1
    seq
  }

  //
  // World interface below
  //

  def spawn(entity_name:String, pos:Vec3, data1:Any = null, data2:Any = null, lifetime:Double = 0) : Entity = {
    //println("game_spawn "+entity_name)
    val entity = new_entity(this, entity_name, pos, data1, data2)
    entities.addOne(entity)

    if ( lifetime != 0 ) {
      entity.die_at = time + lifetime
    }

    entity
  }

  def init_level(level:Int) = {

    randomInit()

    display.set_title_message("")
    entities.clear()
    map_index=level
    map.init(map_data(map_index))
  }

  def reset_level() = {
    init_level(map_index)

    //debug: switch to replay
    /*
    this.mode = "replay"
    player.input = new InputRemote()
    randomInit()
    log.state.reset()
    */
  }

  def next_level() = {
    jump_to_next_level = true
  }

  def no_entity_needs_key() = {
    for (e <- entities) {
      if (e.needs_key) {
        e.needs_key = false
      }
    }
  }

  val log = new GameLog()
  var mode = "record"

  def syncState() = {

    if ( mode == "replay" ) {
      log.loadState(this)
    }

    if ( mode == "record") {
      log.saveState(this)
    }
  }

}
