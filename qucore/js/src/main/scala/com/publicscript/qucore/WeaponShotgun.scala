package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}
import com.publicscript.qucore.Resources.{model_shotgun,sfx_shotgun_shoot,sfx_shotgun_reload}

import scala.scalajs.js.timers._

class WeaponShotgun(world:World) extends Weapon(world) {

  this.texture = 7
  this.model = model_shotgun
  this.sound = sfx_shotgun_shoot
  this.needs_ammo = false
  this.reload = 0.9
  this.projectile_type = "shell"
  this.projectile_speed = 10000

  override def spawn_projectile(pos: Vec3, yaw: Double, pitch: Double) = {

    setTimeout(200) {
      world.audio_play(sfx_shotgun_reload)
    }
    setTimeout(350) {
      world.audio_play(sfx_shotgun_reload)
    }

    for (i <- 0 until 8) {
      super.spawn_projectile(pos, yaw + world.random() * 0.08 - 0.04, pitch + world.random() * 0.08 - 0.04)
    }
  }

}


