package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3, vec3, vec3_add, vec3_rotate_yaw_pitch}
import org.scalajs.dom.AudioBuffer


class Weapon(world:World)  {

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
    this.world.play_sound(this.sound)
    this.spawn_projectile(pos, yaw, pitch)
  }

  def spawn_projectile(pos: Vec3, yaw: Double, pitch: Double) = {
    val projectile = world.spawn(this.projectile_type, vec3_add(pos, vec3_add(vec3(0, 12, 0), vec3_rotate_yaw_pitch(this.projectile_offset, yaw, pitch))))
    // Set the projectile velocity, yaw and pitch
    projectile.veloc = vec3_rotate_yaw_pitch(vec3(0, 0, this.projectile_speed), yaw, pitch)
    projectile.yaw = yaw - Math.PI / 2
    projectile.pitch = -pitch
    projectile.check_against = "enemy"
    // Alternate left/right fire for next projectile (nailgun)
    this.projectile_offset.x *= -1
  }

}


