package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,  vec3, vec3_dist, vec3_2d_angle}

abstract class WorldEntity(val world:World, var pos: Vec3) extends Serializable {

  var tag = world.nextSeq()

  var accel = vec3()
  var veloc = vec3()
  var size = vec3(2, 2, 2)

  var yaw: Double = 0
  var pitch: Double = 0

  var health: Double = 50
  var dead: Boolean = false
  var die_at: Double = 0


  def update() : Unit




  //serializable interface
  def writeState(outputState:State) = {

    outputState.writeInt(this.tag)

    outputState.writeVec3(this.pos)
    outputState.writeVec3(this.accel)
    outputState.writeVec3(this.veloc)
    outputState.writeVec3(this.size)

    outputState.writeFloat(this.yaw)
    outputState.writeFloat(this.pitch)

    outputState.writeBool(this.dead)

    outputState.writeFloat(this.health)
    outputState.writeFloat(this.die_at)
  }

  def readState(inputState:State) = {

    inputState.readVec3(this.pos)
    inputState.readVec3(this.accel)
    inputState.readVec3(this.veloc)
    inputState.readVec3(this.size)

    this.yaw = inputState.readFloat()
    this.pitch = inputState.readFloat()

    this.dead = inputState.readBool()

    this.health = inputState.readFloat()
    this.die_at = inputState.readFloat()
  }


  //utility functions
  override def toString: String = {
    var ret = this.getClass.getSimpleName
    ret = ret + s" (pos=${pos.x.round},${pos.y.round},${pos.z.round})(dead=$dead)"
    return ret
  }

  def get_distance_to_player(): Double = {
    vec3_dist(this.pos, this.world.player.pos)
  }

  def get_angle_to_player(): Double = {
    vec3_2d_angle(this.pos, this.world.player.pos)
  }

  def line_of_sight_to_player(): Boolean = {
    world.map.line_of_sight(this.pos, this.world.player.pos)
  }

}

