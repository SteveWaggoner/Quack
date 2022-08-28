package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3_add,vec3}
import com.publicscript.qucore.Resources.{model_grunt,sfx_shotgun_shoot}


class EntityEnemyGrunt(world:World, pos: Vec3, patrol_dir: Double) extends EntityEnemy(world, pos, patrol_dir) {

  model = Some(model_grunt)
  texture = Some(17)
  health = 40

  def attack() = {
    this.play_sound(sfx_shotgun_shoot)
    world.spawn("light", vec3_add(this.pos, vec3(0, 30, 0)), 10, 0xff, lifetime=0.1)
    for (i <- 0 until 3) {
      this.spawn_projectile("shell", 10000, world.random() * 0.08 - 0.04, world.random() * 0.08 - 0.04)
    }
  }

}
