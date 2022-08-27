package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3, vec3_2d_angle, vec3_dist}
import com.publicscript.qucore.Resources.map_data
import org.scalajs.dom.AudioBuffer

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js.timers.{SetTimeoutHandle, clearTimeout, setTimeout}


class GameWorld extends World {

  private var message_timeout : SetTimeoutHandle = _
  var jump_to_next_level = false

  var entities = new ArrayBuffer[Entity](0)
  var player : EntityPlayer = _
  var map = new Map()
  var map_index : Int = _

  val audio = new Audio()
  val render = new Render(Document.c)
  var model = new Model(render)

  def show_title_message(msg: String, sub: String = "") = {

    if ( msg == "" && sub == "" ) {
      Document.ts.style.display = "none"
    } else {
      Document.ts.innerHTML = "<h1>" + msg + "</h1>" + sub
      Document.ts.style.display = "block"
    }
  }
  def show_game_message(text: String) = {
    Document.msg.textContent = text
    Document.msg.style.display = "block"
    clearTimeout(message_timeout)
    message_timeout = setTimeout(2000) { Document.msg.style.display = "none" }
  }

  def show_health(health: String) = {
    Document.h.textContent = health
  }

  def show_ammo(ammo: String) = {
    Document.a.textContent = ammo
  }

  def mouse_sensitivity(): Double = {
    Document.m.value.toDouble
  }

  def mouse_inverted() : Boolean = {
    Document.mi.checked
  }

  def audio_play(buffer: AudioBuffer, volume: Double = 1, loop: Boolean = false, pan: Double = 0) = {
    audio.play(buffer, volume, loop, pan)
  }
  def render_draw(pos: Vec3, yaw: Double, pitch: Double, texture: Int, offset1: Int, offset2: Int, mix: Int, num_verts: Int) = {
    render.draw(pos, yaw, pitch, texture, offset1, offset2, mix, num_verts)
  }
  def render_light(pos: Vec3, intensity: Double, r: Double, g: Double, b: Double) = {
    render.push_light(pos, intensity, r, g, b)
  }

  def get_distance_to_camera(pos: Vec3) : Double = {
    vec3_dist(pos, render.camera)
  }
  def get_angle_to_camera(pos:Vec3) : Double = {
    vec3_2d_angle(pos, render.camera) - render.camera_yaw
  }

  def camera(): Vec3 = {
    render.camera
  }

  def camera_pitch():Double = {
    render.camera_pitch
  }
  def camera_yaw():Double = {
    render.camera_yaw
  }
  def camera_pitch_=(pitch: Double) = {
    render.camera_pitch = pitch
  }
  def camera_yaw_=(yaw: Double) = {
    render.camera_yaw = yaw
  }


  def map_line_of_sight(a:Vec3, b:Vec3) : Boolean = {
    map.line_of_sight(a,b)
  }
  def map_block_at_box(box_start: Vec3, box_end: Vec3) : Boolean = {
    map.block_at_box(box_start, box_end)
  }

  def map_block_beneath(pos:Vec3, size:Vec3) : Boolean = {
    map.block_beneath(pos, size)
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
      case "player" => new EntityPlayer(world, pos, data1, data2)
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

  var time = 0.016d
  var tick = 0d
  private var real_time_last: Double = 0


  //debugging
  var frames = 0
  var elapsedTime = 0d


  def set_time_now(time_now_par:Double) = {
    var time_now = time_now_par

    time_now *= 0.001

    if (real_time_last == 0)
      real_time_last = time_now

    tick = Math.min(time_now - real_time_last, 0.05)
    real_time_last = time_now
    time += tick


    elapsedTime = elapsedTime + tick
    frames = frames + 1
  }

  //debugging
  def framesPerSecond() : Double = {
    frames / elapsedTime
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
    show_title_message("")
    entities.clear()
    map_index=level
    map.init(map_data(map_index))
  }

  def reset_level() = {
    init_level(map_index)
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

  def get_line_of_sight(a:Vec3, b:Vec3) : Boolean = {
    map.line_of_sight(a,b)
  }

}
