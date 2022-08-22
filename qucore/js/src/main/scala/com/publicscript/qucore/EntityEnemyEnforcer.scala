package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3}
import com.publicscript.qucore.Resources.{model_enforcer,sfx_plasma_shoot}


class EntityEnemyEnforcer(world:World, pos:Vec3, patrol_dir:Double) extends EntityEnemy(world, pos, patrol_dir) {

  this.model = Some(model_enforcer)
  this.texture = Some(19)
  this.health = 80
  this.size = vec3(14, 44, 14)


  def attack() = {
    this.play_sound(sfx_plasma_shoot)
    this.spawn_projectile("plasma", 800, 0, 0)
  }

}


