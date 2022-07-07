package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_dist,vec3_2d_angle,vec3_rotate_y,anglemod,vec3_rotate_yaw_pitch}
import com.publicscript.qucore.Game.{game_time,game_entities_enemies,game_entity_player,game_spawn,map}

import com.publicscript.qucore.Resources.{model_blood,sfx_enemy_hit,model_gib_pieces,sfx_enemy_gib}

abstract class EntityEnemy(apos: Vec3,patrol_dir: Double) extends Entity(apos) {

  case class State(anim_index: Int, speed: Double, next_state_update: Double, next_state: State = null, name:String="")

  // Animations
  var ANIMS = Array(
    Anim(1, Array(0)), // 0: Idle
    Anim(0.40, Array(1, 2, 3, 4)), // 1: Walk
    Anim(0.20, Array(1, 2, 3, 4)), // 2: Run
    Anim(0.25, Array(0, 5, 5, 5)), // 3: Attack prepare
    Anim(0.25, Array(5, 0, 0, 0)) // 4: Attack
  )

  // State definitions
  // [0: anim_index, 1: speed, 2: next_state_update, 3: next_state]
  val STATE_IDLE = State( 0, 0, 0.1, name="IDLE")
  var STATE_PATROL = State(1, 0.5, 0.5, name="PATROL")
  var STATE_FOLLOW = State(2, 1, 0.3, name="FOLLOW")
  var STATE_ATTACK_RECOVER = State(0, 0, 0.1, this.STATE_FOLLOW, name="ATTACK_RECOVER")
  var STATE_ATTACK_EXEC = State(4, 0, 0.4, this.STATE_ATTACK_RECOVER, name="ATTACK_EXEC")
  var STATE_ATTACK_PREPARE = State(3, 0, 0.4, this.STATE_ATTACK_EXEC, name="ATTACK_PREPARE")
  var STATE_ATTACK_AIM = State(0, 0, 0.1, this.STATE_ATTACK_PREPARE, name="ATTACK_AIM")
  var STATE_EVADE = State(2, 1, 0.8, this.STATE_ATTACK_AIM, name="EVADE")

  this.size = vec3(12, 28, 12)
  this.step_height = 17
  this.keep_off_ledges = true
  this.check_against = Entity.ENTITY_GROUP_PLAYER

 // var speed = 196

  var speed = 196 / 4

  var attack_distance = 800
  var evade_distance = 96
  var attack_chance = 0.65

  var state: State = STATE_IDLE
  var state_update_at = 0d
  var target_yaw = this.yaw
  var turn_bias = 1d

  needs_key = false


  game_entities_enemies.addOne(this)
  // If patrol_dir is non-zero it determines the partrol direction in
  // increments of 90°. Otherwise we just idle.
  if (patrol_dir != 0) {
    this.set_state(this.STATE_PATROL)
    this.target_yaw = Math.PI / 2 * patrol_dir
    this.anim_time = Math.random()
  } else {
    this.set_state(this.STATE_IDLE)
  }

  def set_state(state: State) = {
    this.state = state
    this.anim = this.ANIMS(state.anim_index)
    this.anim_time = 0
    this.state_update_at = game_time + state.next_state_update + state.next_state_update / 4d * Math.random()
  }

  override def update() = {
    // Is it time for a state update?
    if (this.state_update_at < game_time) {
      // Choose a new turning bias for FOLLOW/EVADE when we hit a wall
      this.turn_bias = if (Math.random() > 0.5) 0.5 else -0.5
      val distance_to_player = vec3_dist(this.pos, game_entity_player.pos)
      val angle_to_player = vec3_2d_angle(this.pos, game_entity_player.pos)

      if (this.state.next_state != null) {
        this.set_state(this.state.next_state)
      }
      // Try to minimize distance to the player
      if (this.state == this.STATE_FOLLOW) {
        // Do we have a line of sight?
        if (map.line_of_sight(this.pos, game_entity_player.pos) ) {
          this.target_yaw = angle_to_player
        }
        // Are we close enough to attack?
        if (distance_to_player < this.attack_distance) {
          // Are we too close? Evade!
          if (distance_to_player < this.evade_distance || Math.random() > this.attack_chance) {
            this.set_state(this.STATE_EVADE)
            this.target_yaw += Math.PI / 2 + Math.random() * Math.PI
          } else
          // Just the right distance to attack!
          {
            this.set_state(this.STATE_ATTACK_AIM)
          }
        }
      }
      // We just attacked; just keep looking at the player 0_o
      if (this.state == this.STATE_ATTACK_RECOVER) {
        this.target_yaw = angle_to_player
      }
      // Wake up from patroling or idlyng if we have a line of sight
      // and are near enough
      if (this.state == this.STATE_PATROL || this.state == this.STATE_IDLE) {
        if (distance_to_player < 700 && map.line_of_sight(this.pos, game_entity_player.pos) ) {
          this.set_state(this.STATE_ATTACK_AIM)
        }
      }
      // Aiming - reorient the entity towards the player, check
      // if we have a line of sight
      if (this.state == this.STATE_ATTACK_AIM) {
        this.target_yaw = angle_to_player
        // No line of sight? Randomly shuffle around :/
        if (map.no_line_of_sight(this.pos, game_entity_player.pos)) {
          this.set_state(this.STATE_EVADE)
        }
      }
      // Execute the attack!
      if (this.state == this.STATE_ATTACK_EXEC) {
        this.attack()
      }
    }
    // Rotate to desired angle
    this.yaw += anglemod(this.target_yaw - this.yaw) * 0.1
    // Move along the yaw direction with the current speed (which might be 0)
    if (this.on_ground) {
      this.veloc = vec3_rotate_y(vec3(0, this.veloc.y, this.state.speed * this.speed), this.target_yaw)
    }
    this.update_physics()
    this.draw_model()
  }

  def attack(): Unit

  def spawn_projectile(entity_name: String, speed: Double, yaw_offset: Double, pitch_offset: Double) : Entity = {
    val projectile = game_spawn(entity_name, this.pos)
    projectile.check_against = Entity.ENTITY_GROUP_PLAYER
    projectile.yaw = this.yaw + Math.PI / 2
    projectile.veloc = vec3_rotate_yaw_pitch(vec3(0, 0, speed), this.yaw + yaw_offset, Math.atan2(this.pos.y - game_entity_player.pos.y, vec3_dist(this.pos, game_entity_player.pos)) + pitch_offset)
    projectile
  }

  override def receive_damage(from: Any, amount: Double) = {
    super.receive_damage(from, amount)
    this.play_sound(sfx_enemy_hit)
    // Wake up if we're idle or patrolling
    if (this.state == this.STATE_IDLE || this.state == this.STATE_PATROL) {
      this.target_yaw = vec3_2d_angle(this.pos, game_entity_player.pos)
      this.set_state(this.STATE_FOLLOW)
    }
    this.spawn_particles(2, 200, model_blood, 18, 0.5)
  }

  override def kill() = {
    super.kill()
    for (m <- model_gib_pieces) {
      this.spawn_particles(2, 300, m, 18, 1)
    }
    this.play_sound(sfx_enemy_gib)
    game_entities_enemies = game_entities_enemies.filter((e: Entity) => e != this)
  }

  override def did_collide(axis: Int): Unit = {
    if (axis == 1) {
      return
    }
    // If we hit a wall/ledge while patrolling just turn around 180
    if (this.state == this.STATE_PATROL) {
      this.target_yaw += Math.PI
    } else {
      this.target_yaw += this.turn_bias
    }
  }

}

