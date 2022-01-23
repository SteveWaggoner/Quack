package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_zombie,sfx_enemy_hit}


class EntityEnemyZombie(apos:Vec3,apatrol_dir:Double) extends EntityEnemy(apos,apatrol_dir) {

  this.model = Some(model_zombie)
  this.texture = Some(18)
  this.speed = 0
  this.attack_distance = 350
  this.health = 60
  this.ANIMS(3) = Anim(0.25, Array(0, 0, 5, 5)) // 3: Attack prepare
  this.STATE_FOLLOW = State(0, 0, 0.1)
  this.STATE_ATTACK_RECOVER = State(0, 0, 1.1, this.STATE_IDLE)
  this.STATE_ATTACK_EXEC = State(4, 0, 0.4, this.STATE_ATTACK_RECOVER)
  this.STATE_ATTACK_PREPARE = State(3, 0, 0.4, this.STATE_ATTACK_EXEC)
  this.STATE_ATTACK_AIM = State(0, 0, 0.1, this.STATE_ATTACK_PREPARE)
  this.STATE_EVADE = State(0, 0, 0.1, this.STATE_ATTACK_AIM)
  this.set_state(this.STATE_IDLE)


  override def receive_damage(from: Any, amount: Double) = {
    // Ignore damage that's not large enough to gib us
    if (amount > 60) {
      super.receive_damage(from, amount)
    }
  }

  def attack() = {
    this.play_sound(sfx_enemy_hit)
    this.spawn_projectile("gib", 600, 0, -0.5)
  }

}
