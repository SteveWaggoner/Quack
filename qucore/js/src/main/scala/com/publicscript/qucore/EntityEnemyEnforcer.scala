package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3}
import com.publicscript.qucore.Main.{model_enforcer,sfx_plasma_shoot}


class EntityEnemyEnforcer(apos:Vec3, apatrol_dir:Double) extends EntityEnemy(apos, apatrol_dir) {

  this.model = Some(model_enforcer)
  this.texture = Some(19)
  this.health = 80
  this.size = vec3(14, 44, 14)


  def attack() = {
    this.play_sound(sfx_plasma_shoot)
    this.spawn_projectile("plasma", 800, 0, 0)
  }

}


