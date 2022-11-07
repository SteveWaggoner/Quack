package com.publicscript.qucore

import com.publicscript.qucore.MathUtils._
import com.publicscript.qucore.Resources.{sfx_hurt, sfx_no_ammo}

import scala.scalajs.js.timers._

import scala.collection.mutable.ArrayBuffer



class EntityPlayer(world:World, ap: Vec3, p1: Any, p2: Any, var input:Input) extends Entity(world, ap) {

  //constructor
  size = vec3(12, 24, 12)
  friction = 10
  val speed = 3000d
  step_height = 17
  var can_jump = false
  var can_shoot_at = 0d
  health = 100
  check_against = "enemy"
  val weapons = new ArrayBuffer[Weapon]()
  weapons.addOne(new WeaponShotgun(world))
  var weapon_index = 0

  // Map 1 needs some rotation of the starting look-at direction
  yaw += world.map_index * Math.PI
  var bob = 0d
  world.player = this

  is_friend = true


  override def update() = {

    // Mouse look
    this.pitch = clamp(this.pitch + input.mouse_y * world.display.get_mouse_sensitivity() * (if (world.display.get_mouse_inverted()) -0.00015 else 0.00015), -1.5, 1.5)
    this.yaw = (this.yaw + input.mouse_x * world.display.get_mouse_sensitivity() * 0.00015) % (Math.PI * 2)
    // Acceleration in movement direction
    val key_x = (if (input.key_right) 1 else 0) - (if (input.key_left) 1 else 0)
    val key_y = (if (input.key_up) 1 else 0) - (if (input.key_down) 1 else 0)

    this.accel = vec3_mulf(vec3_rotate_y(vec3(key_x, 0, key_y), this.yaw), this.speed * (if (this.on_ground) 1.0 else 0.3))

    if (input.key_jump && this.on_ground && this.can_jump) {
      this.veloc.y = 400
      this.on_ground = false
      this.can_jump = false
    }
    if (!input.key_jump) {
      this.can_jump = true
    }

    val key_change = (if (input.key_next) 1 else 0) - (if (input.key_prev) 1 else 0)

    this.weapon_index = (this.weapon_index + key_change + this.weapons.length) % this.weapons.length
    val shoot_wait = this.can_shoot_at - world.time
    val weapon = this.weapons(this.weapon_index)

    // Shoot Weapon
    if (input.key_action && shoot_wait < 0) {
      this.can_shoot_at = world.time + weapon.reload
      if (weapon.needs_ammo && weapon.ammo == 0) {
        world.play_sound(sfx_no_ammo)
      } else {
        weapon.shoot(this.pos, this.yaw, this.pitch)
        world.spawn("light", this.pos, 10, 0xff, lifetime=0.1)
      }
    }
    this.bob += vec3_length(this.accel) * 0.0001
    this.friction = if (this.on_ground) 10 else 2.5
    this.update_physics()

    world.display.render.camera.x = this.pos.x
    world.display.render.camera.z = this.pos.z
    // Smooth step up on stairs
    world.display.render.camera.y = this.pos.y + 8 - clamp(world.time - this.stepped_up_at, 0, 0.1) * -160
    world.display.render.camera_yaw = this.yaw
    world.display.render.camera_pitch = this.pitch
    // Draw weapon at camera position at an offset and add the current
    // recoil (calculated from shoot_wait and weapon._reload) accounting
    // for the current view yaw/pitch


    world.display.render.draw(vec3_add(world.display.render.camera, vec3_rotate_yaw_pitch(vec3(0, -10d + Math.sin(this.bob) * 0.3, 12d + clamp(scale(shoot_wait, 0, weapon.reload, 5, 0), 0, 5)), this.yaw, this.pitch)),
      this.yaw + Math.PI / 2, this.pitch,
      weapon.texture, weapon.model.frames(0), weapon.model.frames(0), 0, weapon.model.num_verts)

    world.display.set_health(this.health.toString)
    world.display.set_ammo( if (weapon.needs_ammo) weapon.ammo.toString else "∞" )

    // Debug: a light around the player
    // r_push_light(vec3_add(this.p, vec3(0,64,0)), 10, 255, 192, 32);
  }

  override def receive_damage(from: Entity, amount: Double) = {
    world.play_sound(sfx_hurt)
    super.receive_damage(from, amount)
  }

  override def kill() = {
    super.kill()
    world.display.set_health( this.health.toString )
    world.display.set_title_message("YOU DIED")
    setTimeout(2000) {
      world.reset_level()
    }

  }

}

