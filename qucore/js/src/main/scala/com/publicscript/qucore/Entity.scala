package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3, clamp, scale, vec3, vec3_dist, vec3_2d_angle,vec3_sub,vec3_add,vec3_mulf,vec3_mul,vec3_length,vec3_clone}
import com.publicscript.qucore.Game.{game_tick, game_time,game_spawn,game_entities_friendly,game_entities_enemies}
import com.publicscript.qucore.Audio.audio_play
import com.publicscript.qucore.Render.{r_draw,r_camera,r_camera_yaw}
import com.publicscript.qucore.Model.Model
import com.publicscript.qucore.Map.{map_block_at,map_block_at_box,map_block_beneath}
import org.scalajs.dom.AudioBuffer


object Entity {

  val ENTITY_GROUP_NONE = 0
  val ENTITY_GROUP_PLAYER = 1
  val ENTITY_GROUP_ENEMY = 2
  //Entity Factory
  def apply(entity_name:String, pos:Vec3, data1:Any, data2:Any) : Entity = {

    // Entity Id to class - must be consistent with map_packer.c line ~900


  //  println("creating a "+entity_name)

    entity_name match {
      case "player" => new EntityPlayer(pos, data1, data2)
      case "grunt" => new EntityEnemyGrunt(pos, data1.asInstanceOf[Double])

      case "enforcer" => new EntityEnemyGrunt(pos, data1.asInstanceOf[Double])
      case "ogre" => new EntityEnemyGrunt(pos, data1.asInstanceOf[Double])
      case "zombie" => new EntityEnemyZombie(pos, data1.asInstanceOf[Double])
      case "hound" => new EntityEnemyHound(pos, data1.asInstanceOf[Double])
      case "nailgun" => new EntityPickupNailgun(pos)
      case "grenadelauncher" => new EntityPickupGrenadeLauncher(pos)
      case "health" => new EntityPickupHealth(pos)
      case "nails" => new EntityPickupNails(pos)
      case "grenades" => new EntityPickupGrenades(pos)
      case "barrel" => new EntityBarrel(pos)
      case "light" => new EntityLight(pos, data1.asInstanceOf[Double],data2.asInstanceOf[Int])
      case "trigger_level" => new EntityTriggerLevel(pos)
      case "door" => new EntityDoor(pos, data1.asInstanceOf[Int], data2.asInstanceOf[Double])
      case "pickupkey" => new EntityPickupKey(pos)
      case "torch" => new EntityTorch(pos)

      case "gib" => new EntityProjectileGib(pos)
      case "grenade" => new EntityProjectileGrenade(pos)
      case "nail" => new EntityProjectileNail(pos)
      case "plasma" => new EntityProjectilePlasma(pos)
      case "shell" => new EntityProjectileShell(pos)


      /*
            case 2 => Some(new EntityEnemyEnforcer(p, e.data1, e.data2))
            case 3 => Some(new EntityEnemyOgre(p, e.data1, e.data2))
            case 4 => Some(new EntityEnemyZombie(p, e.data1, e.data2))
            case 5 => Some(new EntityEnemyHound(p, e.data1, e.data2))
            case 6 => Some(new EntityPickupNailgun(p, e.data1, e.data2))
            case 7 => Some(new EntityPickupGrenadeLauncher(p, e.data1, e.data2))
            case 8 => Some(new EntityPickupHealth(p, e.data1, e.data2))
            case 9 => Some(new EntityPickupNails(p, e.data1, e.data2))
            case 10 => Some(new EntityPickupGrenades(p, e.data1, e.data2))
            case 11 => Some(new EntityBarrel(p, e.data1, e.data2))
            case 12 => Some(new EntityLight(p, e.data1, e.data2))
            case 13 => Some(new EntityTriggerLevel(p, e.data1, e.data2))
            case 14 => Some(new EntityDoor(p, e.data1, e.data2))
            case 15 => Some(new EntityPickupKey(p, e.data1, e.data2))
            case 16 => Some(new EntityTorch(p, e.data1, e.data2))
      */

      case _ => throw new IllegalArgumentException(s"Unknown entity name: $entity_name")
    }
  }


}

class Entity(var pos: Vec3) {

  override def toString: String = {
    var ret = super.toString
    ret = ret + s" (pos=${pos.x},${pos.y},${pos.z})"
    return ret
  }

    case class Anim(var speed:Double, var frame:Array[Int])

    var model : Option[Model] = None
    var check_entities = Array.empty[Entity]
    var check_against: Int = Entity.ENTITY_GROUP_NONE
    var texture : Option[Int] = None

    var accel = vec3()
    var veloc = vec3()
    var size = vec3(2, 2, 2)
    var f: Double = 0
    var health: Double = 50
    var dead: Boolean = false
    var die_at: Double = 0
    var step_height: Double = 0
    var bounciness: Double = 0
    var gravity: Double = 1
    var yaw: Double = 0
    var pitch: Double = 0
    var anim = Anim(1, Array(0))
    var anim_time: Double = Math.random()
    var on_ground: Boolean = false
    var keep_off_ledges: Boolean = false
    var stepped_up_at: Double = 0

    var needs_key = false

    def update() = {
      if (model.isDefined) {
        draw_model()
      }
    }

    def update_physics() : Unit = {

      if ( this.getClass.getSimpleName == "EntityPlayer" ) {
        println(" update_physics()")
      }

      if (die_at != 0 && die_at < game_time) {
        kill()
        return
      }
      // Apply Gravity
      this.accel.y = -1200 * gravity
      // Integrate acceleration & friction into velocity
      val ff = Math.min(this.f * game_tick, 1)
      this.veloc = vec3_add(this.veloc, vec3_sub(vec3_mulf(this.accel, game_tick), vec3_mul(this.veloc, vec3(ff, 0, ff))))

      // Set up the _check_entities array for entity collisions
      this.check_entities = check_against match {
        case Entity.ENTITY_GROUP_NONE => Array()
        case Entity.ENTITY_GROUP_PLAYER => game_entities_friendly.toArray
        case Entity.ENTITY_GROUP_ENEMY => game_entities_enemies.toArray
      }


      // Divide the physics integration into 16 unit steps; otherwise fast
      // projectiles may just move through walls.
      val original_step_height = this.step_height
      val move_dist = vec3_mulf(this.veloc, game_tick)
      val steps = Math.ceil(vec3_length(move_dist) / 16).toInt

      //debug code ---
      /*
      if ( steps == 0 ) {
        println("update_physics nothing to move entity="+this)

        println(" veloc ="+this.veloc)
        println(" game_tick = "+game_tick)
        println(" move_dist ="+move_dist)
        println("  vec3_length(move_dist)="+vec3_length(move_dist))

        return
      }
      */
      //----
      if ( steps == 0) {
        println("no steps..not running update_physics()")
        return
      }

      val move_step = vec3_mulf(move_dist, 1.0 / steps)
      var s = 0

      if ( this.getClass.getSimpleName == "EntityPlayer" ) {
        println(" update_physics() s=" + s + "  steps=" + steps)
      }

      while (s < steps) {

        // Remember last position so we can roll back
        val last_pos = vec3_clone(this.pos)
        // Integrate velocity into position
        this.pos = vec3_add(this.pos, move_step)

        if ( this.getClass.getSimpleName == "EntityPlayer" ) {
          println("  s=" + s + " move_step=" + move_step + " pos=" + pos + " last_pos=" + last_pos)
        }

        // Collision with walls, horizonal
        if (this.collides(vec3(this.pos.x, last_pos.y, last_pos.z))) {
          // Can we step up?
          if (this.step_height!=0 || !this.on_ground || this.veloc.y > 0 || this.collides(vec3(this.pos.x, last_pos.y + this.step_height, last_pos.z))) {
            this.did_collide(0)
            this.pos.x = last_pos.x
            this.veloc.x = -this.veloc.x * this.bounciness
          } else {
            last_pos.y += this.step_height
            this.stepped_up_at = game_time
          }
          s = steps // stop after this iteration
        }
        // Collision with walls, vertical
        if (this.collides(vec3(this.pos.x, last_pos.y, this.pos.z))) {
          // Can we step up?
          if (this.step_height==0 || !this.on_ground || this.veloc.y > 0 || this.collides(vec3(this.pos.x, last_pos.y + this.step_height, this.pos.z))) {
            this.did_collide(2)
            this.pos.z = last_pos.z
            this.veloc.z = -this.veloc.z * this.bounciness
          } else {
            last_pos.y += this.step_height
            this.stepped_up_at = game_time
          }
          s = steps // stop after this iteration
        }
        // Collision with ground/Ceiling
        if (this.collides(this.pos)) {
          this.did_collide(1)
          this.pos.y = last_pos.y
          // Only bounce from ground/ceiling if we have enough velocity
          val bounce = if (Math.abs(this.veloc.y) > 200) this.bounciness else 0
          this.on_ground = this.veloc.y < 0 && bounce==0
          this.veloc.y = -this.veloc.y * bounce
          s = steps // stop after this iteration
        }
        this.step_height = original_step_height

        s += 1
      }
    }

    def collides(p: Vec3): Boolean = {
      if (dead) {
        return false
      }
      for (entity <- check_entities) {
        if (vec3_dist(p, entity.pos) < this.size.y + entity.size.y) {
          // If we collide with an entity set the step height to 0,
          // so we don't climb up on its shoulders :/
          step_height = 0
          did_collide_with_entity(entity)
          return true
        }
      }
      // Check if there's no block beneath this point. We want the AI to keep
      // off of ledges.
      if (on_ground && keep_off_ledges && !map_block_beneath(p, this.size)) {
        return true
      }
      // Do the normal collision check with the whole box
      map_block_at_box(vec3_sub(p, this.size), vec3_add(p, this.size))
    }

    def did_collide(axis: Double) = {
   //   false
    }

    def did_collide_with_entity(other: Entity) = {
   //   false
    }

    def draw_model() = {

      this.anim_time += game_tick
      // Calculate which frames to use and how to mix them
      val f = (anim_time / anim.speed).toInt
      var mix = 0 // f - (f | 0) // <- what is this doing????? just make zero for now
      var frame_cur : Int = this.anim.frame(f % anim.frame.length)
      var frame_next : Int = this.anim.frame((f + 1) % this.anim.frame.length)
      // Swap frames if we're looping to the first frame again
      if (frame_next < frame_cur) {

        val tmp = frame_cur
        frame_cur = frame_next
        frame_next = tmp

        mix = 1 - mix
      }

      r_draw(pos, yaw, pitch, texture.get, model.get.f(frame_cur), model.get.f(frame_next), mix, model.get.nv)

    }

    def spawn_particles(amount: Int, speed: Double = 1, model: Model, texture: Int, lifetime: Double) = {
      for (i <- 0 until amount) {
        val particle = game_spawn("particle", pos)
        particle.model = Some(model)
        particle.texture = Some(texture)
        particle.die_at = (game_time + lifetime + Math.random() * lifetime * 0.2).toInt
        particle.veloc = vec3((Math.random() - 0.5) * speed, Math.random() * speed, (Math.random() - 0.5) * speed)
      }
    }

    def receive_damage(from: Any, amount: Double) = {
      if (!dead) {
        health -= amount
        if (health <= 0) {
          kill()
        }
      }
    }

    def play_sound(sound: AudioBuffer) = {
      val volume = clamp(scale(vec3_dist(this.pos, r_camera), 64, 1200, 1, 0), 0, 1)
      val pan = Math.sin(vec3_2d_angle(this.pos, r_camera) - r_camera_yaw) * -1
      audio_play(sound, volume, false, pan)
    }

    def kill() = {
      dead = true
    }

  }

