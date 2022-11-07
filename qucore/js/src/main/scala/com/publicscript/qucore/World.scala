package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import org.scalajs.dom.AudioBuffer


trait World {

  //game play
  def reset_level()
  def next_level()

  //time interface
  def time() : Double
  def tick() : Double

  //message interface
  def display: Display


  //entity interface
  def spawn(entity_name:String, pos:Vec3, data1:Any = null, data2:Any = null, lifetime:Double = 0) : Entity
  var player : EntityPlayer //TODO: make not assignable
  def get_entity_group(group_name:String) : Array[Entity]
  def no_entity_needs_key()  //TODO: remove

  //map interface
  def map : Map
  def map_index() : Int

  //audio
  def play_sound(sound: AudioBuffer, pos:Vec3 = null)

  //deterministic random (common seed for all players)
  def random() : Double
  //common identifiers for all entitites
  def nextSeq():Int
}
