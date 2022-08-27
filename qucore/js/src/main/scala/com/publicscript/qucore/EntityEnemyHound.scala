package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_rotate_y}
import com.publicscript.qucore.Resources.{model_hound,sfx_enemy_hound_attack}
import scala.scalajs.js.timers._

class EntityEnemyHound(world:World, pos:Vec3, patrol_dir:Double) extends EntityEnemy(world, pos, patrol_dir) {

  this.model = Some(model_hound)
  this.texture = Some(22)
  this.health = 25
  this.check_against = "player"
  this.size = vec3(12, 16, 12)

  this.attack_distance = 200
  this.evade_distance = 64
  this.attack_chance = 0.7
  this.speed = 256

  var did_hit = false
  var reset_ledges: SetTimeoutHandle = _

  this.ANIMS = Array(
    Anim(1, Array(0)), // 0: Idle
    Anim(0.15, Array(0, 1)), // 2: Run
    Anim(0.15, Array(0, 1)), // 2: Run
    Anim(1, Array(0)), // 3: Attack prepare
    Anim(0.1, Array(0, 1, 1, 1, 0, 0, 0)) // 4: Attack
  )

  this.STATE_PATROL = State(1, 0.2, 0.5)
  this.STATE_ATTACK_RECOVER = State(0, 0, 0.5, this.STATE_FOLLOW)
  this.STATE_ATTACK_EXEC = State(4, 0, 1, this.STATE_ATTACK_RECOVER)
  this.STATE_ATTACK_PREPARE = State(3, 0, 0.0, this.STATE_ATTACK_EXEC)
  this.STATE_ATTACK_AIM = State(0, 0, 0.0, this.STATE_ATTACK_PREPARE)
  this.STATE_EVADE = State(2, 1, 0.3, this.STATE_ATTACK_AIM)
  this.set_state(this.STATE_IDLE)


  override def did_collide_with_entity(other: Entity): Unit = {
    if (!this.did_hit && this.state == this.STATE_ATTACK_EXEC) {
      this.did_hit = true
      other.receive_damage(this, 14)
    }
  }

  def attack() = {
    this.play_sound(sfx_enemy_hound_attack)
    this.veloc = vec3_rotate_y(vec3(0, 250, 600), this.target_yaw)
    this.on_ground = false
    this.did_hit = false
    // Ignore ledges while attacking
    this.keep_off_ledges = false
    clearTimeout(this.reset_ledges)
    this.reset_ledges = setTimeout(1000) {
      this.keep_off_ledges = true
    }

  }

}


