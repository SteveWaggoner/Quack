package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_add,vec3_rotate_yaw_pitch}
import com.publicscript.qucore.Game.{game_spawn,audio}
import com.publicscript.qucore.Resources.{model_shotgun,sfx_shotgun_shoot,sfx_shotgun_reload,model_nailgun,sfx_nailgun_shoot,model_grenadelauncher,sfx_grenade_shoot}
import com.publicscript.qucore.Entity.ENTITY_GROUP_ENEMY

import scala.scalajs.js.timers._

import org.scalajs.dom.AudioBuffer

class Weapon {

  var texture = -1
  var model : Model.ModelRender = null
  var reload = 0d

  var ammo = 0
  var sound : AudioBuffer = null
  var projectile_type = "projectile TBD"
  var projectile_speed = 0
  var needs_ammo = true
  var projectile_offset = vec3(0, 0, 8)

  def shoot(pos: Vec3, yaw: Double, pitch: Double) = {
    if (this.needs_ammo) {
      this.ammo -= 1
    }
    audio.play(this.sound)
    this.spawn_projectile(pos, yaw, pitch)
  }

  def spawn_projectile(pos: Vec3, yaw: Double, pitch: Double) = {
    val projectile = game_spawn(this.projectile_type, vec3_add(pos, vec3_add(vec3(0, 12, 0), vec3_rotate_yaw_pitch(this.projectile_offset, yaw, pitch))))
    // Set the projectile velocity, yaw and pitch
    projectile.veloc = vec3_rotate_yaw_pitch(vec3(0, 0, this.projectile_speed), yaw, pitch)
    projectile.yaw = yaw - Math.PI / 2
    projectile.pitch = -pitch
    projectile.check_against = ENTITY_GROUP_ENEMY
    // Alternate left/right fire for next projectile (nailgun)
    this.projectile_offset.x *= -1
  }

}

class WeaponShotgun extends Weapon {

  println(" creating a weaponshotgun model_shotgun="+model_shotgun)

  if (model_shotgun == null) {
    throw new IllegalArgumentException("oops")
  }

    this.texture = 7
    this.model = model_shotgun
    this.sound = sfx_shotgun_shoot
    this.needs_ammo = false
    this.reload = 0.9
    this.projectile_type = "shell"
    this.projectile_speed = 10000

  override def spawn_projectile(pos: Vec3, yaw: Double, pitch: Double) = {

    setTimeout(200) { audio.play(sfx_shotgun_reload) }
    setTimeout(350) { audio.play(sfx_shotgun_reload) }

    for (i <- 0 until 8) {
      super.spawn_projectile(pos, yaw + Math.random() * 0.08 - 0.04, pitch + Math.random() * 0.08 - 0.04)
    }
  }

}

class WeaponNailgun extends Weapon {
    this.texture = 4
    this.model = model_nailgun
    this.sound = sfx_nailgun_shoot
    this.ammo = 100
    this.reload = 0.09
    this.projectile_type = "nail"
    this.projectile_speed = 1300
    this.projectile_offset = vec3(6, 0, 8)

}

class WeaponGrenadeLauncher extends Weapon {

    this.texture = 21
    this.model = model_grenadelauncher
    this.sound = sfx_grenade_shoot
    this.ammo = 10
    this.reload = 0.650
    this.projectile_type = "grenade"
    this.projectile_speed = 900

}


