package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{vec3,Vec3,clamp,vec3_mulf,vec3_rotate_y,vec3_rotate_yaw_pitch,vec3_add,vec3_length,scale}
import com.publicscript.qucore.Game.{game_map_index,game_entity_player,game_entities_friendly,game_spawn,game_time,game_init,title_show_message}


import com.publicscript.qucore.Audio.{audio_play}

import com.publicscript.qucore.Main.{sfx_no_ammo,sfx_hurt}

import com.publicscript.qucore.Render.{r_draw,r_camera,r_camera_yaw,r_camera_pitch}

import com.publicscript.qucore.Entity.ENTITY_GROUP_ENEMY
import com.publicscript.qucore.Input.{mouse_x,mouse_y,key_right,key_left,key_up,key_down,key_jump,key_next,key_prev,key_action}
import scala.scalajs.js.timers._

import com.publicscript.qucore.Document.{m,mi,h,a}


class EntityPlayer(p: Vec3, p1: Any, p2: Any) extends Entity(p) {

  //constructor
  size = vec3(12, 24, 12)
  f = 10
  val speed = 3000
  step_height = 17
  var can_jump = false
  var can_shoot_at = 0d
  health = 100
  check_against = ENTITY_GROUP_ENEMY
  val weapons = Array(new WeaponShotgun())
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

    this.accel = vec3_mulf(vec3_rotate_y(vec3(key_x, 0, key_y), this.yaw), this.speed * (if (this.on_ground) 1 else 0.3))
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
        audio_play(sfx_no_ammo)
      } else {
        weapon.shoot(this.p, this.yaw, this.pitch)
        game_spawn("light", this.p, 10, 0xff).die_at = game_time + 0.1
      }
    }
    this.bob += vec3_length(this.accel) * 0.0001
    this.f = if (this.on_ground) 10 else 2.5
    this.update_physics()
    r_camera.x = this.p.x
    r_camera.z = this.p.z
    // Smooth step up on stairs
    r_camera.y = this.p.y + 8 - clamp(game_time - this.stepped_up_at, 0, 0.1) * -160
    r_camera_yaw = this.yaw
    r_camera_pitch = this.pitch
    // Draw weapon at camera position at an offset and add the current
    // recoil (calculated from shoot_wait and weapon._reload) accounting
    // for the current view yaw/pitch
    r_draw(vec3_add(r_camera, vec3_rotate_yaw_pitch(vec3(0, -10 + Math.sin(this.bob) * 0.3, 12 + clamp(scale(shoot_wait, 0, weapon.reload, 5, 0), 0, 5)), this.yaw, this.pitch)), this.yaw + Math.PI / 2, this.pitch, weapon.texture, weapon.model.f(0), weapon.model.f(0), 0, weapon.model.nv)
    h.textContent = this.health.toString
    a.textContent = if (weapon.needs_ammo) weapon.ammo.toString else "∞"
    // Debug: a light around the player
    // r_push_light(vec3_add(this.p, vec3(0,64,0)), 10, 255, 192, 32);
  }

  override def receive_damage(from: Any, amount: Double) = {
    audio_play(sfx_hurt)
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

