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
  def show_title_message(msg: String, sub: String = "")
  def show_game_message(text: String)
  def show_health(health: String)
  def show_ammo(ammo: String)

  //setting interface
  def mouse_sensitivity() : Double
  def mouse_inverted() : Boolean

  //entity interface
  def spawn(entity_name:String, pos:Vec3, data1:Any = null, data2:Any = null, lifetime:Double = 0) : Entity
  var player : EntityPlayer //TODO: make not assignable
  def get_entity_group(group_name:String) : Array[Entity]
  def no_entity_needs_key()  //TODO: remove

  //map interface
  def map_index() : Int
  def map_line_of_sight(a:Vec3, b:Vec3) : Boolean
  def map_block_at_box(box_start: Vec3, box_end: Vec3) : Boolean
  def map_block_beneath(pos:Vec3, size:Vec3) : Boolean


  //audio
  def audio_play(buffer: AudioBuffer, volume: Double = 1, loop: Boolean = false, pan: Double = 0)

  //video
  def render_draw(pos: Vec3, yaw: Double, pitch: Double, texture: Int, offset1: Int, offset2: Int, mix: Int, num_verts: Int)
  def render_light(pos: Vec3, intensity: Double, r: Double, g: Double, b: Double)
  def get_distance_to_camera(pos: Vec3) : Double
  def get_angle_to_camera(pos:Vec3) : Double

  //camera interface
  def camera() : Vec3
  var camera_yaw : Double
  var camera_pitch : Double

  //deterministic random (common seed for all players)
  def random() : Double
}
