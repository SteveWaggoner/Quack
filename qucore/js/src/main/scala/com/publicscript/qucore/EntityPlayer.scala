package com.publicscript.qucore

import com.publicscript.qucore.MathUtils._
import com.publicscript.qucore.Game._
import com.publicscript.qucore.Resources.{sfx_hurt, sfx_no_ammo}
import com.publicscript.qucore.Entity.ENTITY_GROUP_ENEMY
import com.publicscript.qucore.Input._

import scala.scalajs.js.timers._
import com.publicscript.qucore.Document.{a, h, m, mi}

import scala.collection.mutable.ArrayBuffer



class EntityPlayer(ap: Vec3, p1: Any, p2: Any) extends Entity(ap) {

  //constructor
  size = vec3(12, 24, 12)
  f = 10
  val speed = 3000d
  step_height = 17
  var can_jump = false
  var can_shoot_at = 0d
  health = 100
  check_against = ENTITY_GROUP_ENEMY
  val weapons = new ArrayBuffer[Weapon]()
  weapons.addOne(new WeaponShotgun())
  var weapon_index = 0

  // Map 1 needs some rotation of the starting look-at direction
  yaw += game_map_index * Math.PI
  var bob = 0d
  game_entity_player = this
  game_entities_friendly.addOne(this)


  override def update() = {

    // Mouse look
    this.pitch = clamp(this.pitch + mouse_y * m.value.toDouble * (if (mi.checked) -0.00015 else 0.00015), -1.5, 1.5)
    this.yaw = (this.yaw + mouse_x * m.value.toDouble * 0.00015) % (Math.PI * 2)
    // Acceleration in movement direction
    val key_x = (if (key_right) 1 else 0) - (if (key_left) 1 else 0)
    val key_y = (if (key_up) 1 else 0) - (if (key_down) 1 else 0)

    this.accel = vec3_mulf(vec3_rotate_y(vec3(key_x, 0, key_y), this.yaw), this.speed * (if (this.on_ground) 1.0 else 0.3))

    if (key_jump && this.on_ground && this.can_jump) {
      this.veloc.y = 400
      this.on_ground = false
      this.can_jump = false
    }
    if (!key_jump) {
      this.can_jump = true
    }

    val key_change = (if (key_next) 1 else 0) - (if (key_prev) 1 else 0)

    this.weapon_index = (this.weapon_index + key_change + this.weapons.length) % this.weapons.length
    val shoot_wait = this.can_shoot_at - game_time
    val weapon = this.weapons(this.weapon_index)

    // Shoot Weapon
    if (key_action && shoot_wait < 0) {
      this.can_shoot_at = game_time + weapon.reload
      if (weapon.needs_ammo && weapon.ammo == 0) {
        audio.play(sfx_no_ammo)
      } else {
        println("1weapon.shoot() game_entities.length="+game_entities.length)
        weapon.shoot(this.pos, this.yaw, this.pitch)
        println("2weapon.shoot() game_entities.length="+game_entities.length)

        game_spawn("light", this.pos, 10, 0xff).die_at = game_time + 0.1

        println("3weapon.shoot() game_entities.length="+game_entities.length)
      }
    }
    this.bob += vec3_length(this.accel) * 0.0001
    this.f = if (this.on_ground) 10 else 2.5
    this.update_physics()

    render.camera.x = this.pos.x
    render.camera.z = this.pos.z
    // Smooth step up on stairs
    render.camera.y = this.pos.y + 8 - clamp(game_time - this.stepped_up_at, 0, 0.1) * -160
    render.camera_yaw = this.yaw
    render.camera_pitch = this.pitch
    // Draw weapon at camera position at an offset and add the current
    // recoil (calculated from shoot_wait and weapon._reload) accounting
    // for the current view yaw/pitch


    render.draw(vec3_add(render.camera, vec3_rotate_yaw_pitch(vec3(0, -10d + Math.sin(this.bob) * 0.3, 12d + clamp(scale(shoot_wait, 0, weapon.reload, 5, 0), 0, 5)), this.yaw, this.pitch)),
      this.yaw + Math.PI / 2, this.pitch,
      weapon.texture, weapon.model.frames(0), weapon.model.frames(0), 0, weapon.model.num_verts)

    h.textContent = this.health.toString
    a.textContent = if (weapon.needs_ammo) weapon.ammo.toString else "∞"

    // Debug: a light around the player
    // r_push_light(vec3_add(this.p, vec3(0,64,0)), 10, 255, 192, 32);
  }

  override def receive_damage(from: Any, amount: Double) = {
    audio.play(sfx_hurt)
    super.receive_damage(from, amount)
  }

  override def kill() = {
    super.kill()
    h.textContent = this.health.toString
    title_show_message("YOU DIED")
    setTimeout(2000) {
      game_init(game_map_index)
    }

  }

}

