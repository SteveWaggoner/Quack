package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3_add,vec3}
import com.publicscript.qucore.Resources.{model_grunt,sfx_shotgun_shoot}
import com.publicscript.qucore.Game.{game_spawn,game_time}

class EntityEnemyGrunt(p: Vec3, apatrol_dir: Double) extends EntityEnemy(p,apatrol_dir) {

  model = Some(model_grunt)
  texture = Some(17)
  health = 40

  def attack() = {
    this.play_sound(sfx_shotgun_shoot)
    game_spawn("light", vec3_add(this.p, vec3(0, 30, 0)), 10, 0xff).die_at = game_time + 0.1
    for (i <- 0 until 3) {
      this.spawn_projectile("shell", 10000, Math.random() * 0.08 - 0.04, Math.random() * 0.08 - 0.04)
    }
  }

}
