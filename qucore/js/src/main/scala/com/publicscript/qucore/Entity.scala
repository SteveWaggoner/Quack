package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3, clamp, scale, vec3, vec3_dist, vec3_2d_angle,vec3_sub,vec3_add,vec3_mulf,vec3_mul,vec3_length,vec3_clone}
import com.publicscript.qucore.Model.ModelRender
import org.scalajs.dom.AudioBuffer


import com.publicscript.qucore.Game.{render, audio, map}


object Entity {

  val ENTITY_GROUP_NONE = 0
  val ENTITY_GROUP_PLAYER = 1
  val ENTITY_GROUP_ENEMY = 2
}

class Entity(world:World, var pos: Vec3) {

  override def toString: String = {
    var ret = this.getClass.getSimpleName
    ret = ret + s" (pos=${pos.x.round},${pos.y.round},${pos.z.round})(dead=${dead})"
    return ret
  }

    case class Anim(var speed:Double, var frame:Array[Int])

    var model : Option[ModelRender] = None
    var check_entities = Array.empty[Entity]     //TODO: clean out dead entities
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

    var is_enemy  = false
    var is_friend = false


    def get_distance_to_player() : Double = {
      vec3_dist(this.pos, this.world.player.pos)
    }

    def get_angle_to_player() : Double = {
      vec3_2d_angle(this.pos, this.world.player.pos)
    }

    def line_of_sight_to_player() : Boolean = {
      map.line_of_sight(this.pos, this.world.player.pos)
    }


    def update() = {
      if (model.isDefined) {
        draw_model()
      }
    }

    def update_physics() : Unit = {

      if (die_at != 0 && die_at < world.time) {
        kill()
        return
      }
      // Apply Gravity
      this.accel.y = -1200 * gravity

      // Integrate acceleration & friction into velocity
      val ff = Math.min(this.f * world.tick, 1)
      this.veloc = vec3_add(this.veloc, vec3_sub(vec3_mulf(this.accel, world.tick), vec3_mul(this.veloc, vec3(ff, 0, ff))))

      // Set up the _check_entities array for entity collisions
      this.check_entities = world.get_entity_group(check_against)



      // Divide the physics integration into 16 unit steps; otherwise fast
      // projectiles may just move through walls.
      val original_step_height = this.step_height
      val move_dist = vec3_mulf(this.veloc, world.tick)
      val steps = Math.ceil(vec3_length(move_dist) / 16).toInt

      if ( steps == 0) {
        println("no steps..not running update_physics()")
        return
      }

      val move_step = vec3_mulf(move_dist, 1.0 / steps)
      var s = 0

      while (s < steps) {

        // Remember last position so we can roll back
        val last_pos = vec3_clone(this.pos)
        // Integrate velocity into position
        this.pos = vec3_add(this.pos, move_step)

        // Collision with walls, horizonal
        if (this.collides(vec3(this.pos.x, last_pos.y, last_pos.z))) {
          println("collision with walls, horizonal")

          // Can we step up?
          if (this.step_height!=0 || !this.on_ground || this.veloc.y > 0 || this.collides(vec3(this.pos.x, last_pos.y + this.step_height, last_pos.z))) {
            this.did_collide(0)
            this.pos.x = last_pos.x
            this.veloc.x = -this.veloc.x * this.bounciness
          } else {
            last_pos.y += this.step_height
            this.stepped_up_at = world.time
          }
          s = steps // stop after this iteration
        }
        // Collision with walls, vertical
        if (this.collides(vec3(this.pos.x, last_pos.y, this.pos.z))) {
          println("collision with walls, vertical")

          // Can we step up?
          if (this.step_height==0 || !this.on_ground || this.veloc.y > 0 || this.collides(vec3(this.pos.x, last_pos.y + this.step_height, this.pos.z))) {
            this.did_collide(2)
            this.pos.z = last_pos.z
            this.veloc.z = -this.veloc.z * this.bounciness
          } else {
            last_pos.y += this.step_height
            this.stepped_up_at = world.time
          }
          s = steps // stop after this iteration
        }
        // Collision with ground/Ceiling
        if (this.collides(this.pos)) {
      //    println("collision with ground/Ceiling")


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
        if (!entity.dead) {
          if (vec3_dist(p, entity.pos) < (this.size.y + entity.size.y)) {
            // If we collide with an entity set the step height to 0,
            // so we don't climb up on its shoulders :/
            step_height = 0
            did_collide_with_entity(entity)

            println("entity collides: " + this.toString + " " + entity.toString)

            return true
          }
        }
      }
      // Check if there's no block beneath this point. We want the AI to keep
      // off of ledges.
      if (on_ground && keep_off_ledges && !map.block_beneath(p, this.size)) {
        return true
      }
      // Do the normal collision check with the whole box
      val collided_with_box = map.block_at_box(vec3_sub(p, this.size), vec3_add(p, this.size))
      if ( collided_with_box ) {
     //   println("entity collides with box : "+this.toString)
      }
      return collided_with_box
    }

    def did_collide(axis: Int) = {
   //   false
    }

    def did_collide_with_entity(other: Entity) = {
   //   false
    }

    def draw_model() = {

      this.anim_time += world.tick
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

      render.draw(pos, yaw, pitch, texture.get, model.get.frames(frame_cur), model.get.frames(frame_next), mix, model.get.num_verts)

    }

    def spawn_particles(amount: Int, speed: Double = 1, model: ModelRender, texture: Int, lifetime: Double) = {
      for (i <- 0 until amount) {
        val particle = world.spawn("particle", pos)
        particle.model = Some(model)
        particle.texture = Some(texture)
        particle.die_at = (world.time + lifetime + Math.random() * lifetime * 0.2).toInt
        particle.veloc = vec3((Math.random() - 0.5) * speed, Math.random() * speed, (Math.random() - 0.5) * speed)
      }
    }

    def receive_damage(from: Entity, amount: Double) = {
      if (!dead) {
        health -= amount
        if (health <= 0) {
          kill()
        }
      }
    }

    def play_sound(sound: AudioBuffer) = {
      val volume = clamp(scale(vec3_dist(this.pos, render.camera), 64, 1200, 1, 0), 0, 1)
      val pan = Math.sin(vec3_2d_angle(this.pos, render.camera) - render.camera_yaw) * -1
      audio.play(sound, volume, false, pan)
    }

    def kill() = {
      dead = true
    }

  }

