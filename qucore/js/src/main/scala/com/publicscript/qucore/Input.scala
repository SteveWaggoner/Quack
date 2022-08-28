package com.publicscript.qucore

class Input extends Serializable {

  def dump() = {
    var ret = ""
    if (key_up==true) ret += "[key_up]"
    if (key_down==true) ret += "[key_down]"
    if (key_left==true) ret += "[key_left]"
    if (key_right==true) ret += "[key_right]"
    if (key_prev==true) ret += "[key_prev]"
    if (key_next==true) ret += "[key_next]"
    if (key_action==true) ret += "[key_action]"
    if (key_jump==true) ret += "[key_jump]"

    ret += s" mouse_x=$mouse_x, mouse_y=$mouse_y  last_wheel_event=$last_wheel_event"
    println(ret)
  }

  // We use the ev.code for keyboard input. This contains a string like "KeyW"
  // or "ArrowLeft", which is awkward to use, but it's keyboard layout neutral,
  // so that WASD should work with any layout.
  // We detect the 6th or 3rd char for each of those strings and map them to an
  // in-game button.
  // Movement, Action, Prev/Next, Jump

  var key_up = false
  var key_down = false
  var key_left = false
  var key_right = false
  var key_prev = false
  var key_next = false
  var key_action = false
  var key_jump = false

  def setKey(key: String, pressed: Boolean): Boolean = {
    key match {
      case "W" | "w" | "ArrowUp" => key_up = pressed
      case "A" | "a" | "ArrowLeft" => key_left = pressed
      case "S" | "s" | "ArrowDown" => key_down = pressed
      case "D" | "d" | "ArrowRight" => key_right = pressed
      case "Q" | "q" => key_prev = pressed
      case "E" | "e" => key_next = pressed
      case " " => key_jump = pressed
      case _ => return false
    }
    return true
  }

  var mouse_x = 0d
  var mouse_y = 0d
  var last_wheel_event = -1d


  def writeState(outputState:State) = {
    outputState.writeBool(this.key_up)
    outputState.writeBool(this.key_down)
    outputState.writeBool(this.key_left)
    outputState.writeBool(this.key_right)
    outputState.writeBool(this.key_prev)
    outputState.writeBool(this.key_next)
    outputState.writeBool(this.key_action)
    outputState.writeBool(this.key_jump)

    outputState.writeFloat(this.mouse_x)
    outputState.writeFloat(this.mouse_y)
    outputState.writeFloat(this.last_wheel_event)
  }

  def readState(inputState:State) = {
    this.key_up = inputState.readBool()
    this.key_down = inputState.readBool()
    this.key_left = inputState.readBool()
    this.key_right = inputState.readBool()
    this.key_prev = inputState.readBool()
    this.key_next = inputState.readBool()
    this.key_action = inputState.readBool()
    this.key_jump = inputState.readBool()

    this.mouse_x = inputState.readFloat()
    this.mouse_y = inputState.readFloat()
    this.last_wheel_event = inputState.readFloat()
  }

}