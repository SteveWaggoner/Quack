package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3}
import com.publicscript.qucore.Resources.{model_ogre,sfx_grenade_shoot}

class EntityEnemyOgre(world:World, apos:Vec3, apatrol_dir:Double) extends EntityEnemy(world,apos,apatrol_dir) {


  this.model = Some(model_ogre)
  this.texture = Some(20)
  this.speed = 96
  this.health = 200
  this.size = vec3(14, 36, 14)
  this.attack_distance = 350
  this.ANIMS = Array(
    Anim(1, Array(0)), // 0: Idle
    Anim(0.80, Array(1, 2, 3, 4)), // 1: Walk
    Anim(0.40, Array(1, 2, 3, 4)), // 2: Run
    Anim(0.35, Array(0, 5, 5, 5)), // 3: Attack prepare
    Anim(0.35, Array(5, 0, 0, 0)) // 4: Attack
  )


  override def attack() = {
    this.play_sound(sfx_grenade_shoot)
    this.spawn_projectile("grenade", 600, 0, -0.4) //.damage = 40
  }

}


