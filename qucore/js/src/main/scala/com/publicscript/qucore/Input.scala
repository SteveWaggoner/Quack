package com.publicscript.qucore

import org.scalajs.dom.document
import org.scalajs.dom.{KeyboardEvent, MouseEvent}
import com.publicscript.qucore.Main.game_time
import org.scalajs.dom.html.Canvas


object Input {

  def getCanvas(id: String): Option[Canvas] = {
    val queryResult = document.querySelector(s"#$id")
    queryResult match {
      case canvas: Canvas => Some(canvas)
      case other =>
        println(s"Element with ID $id is not an canvas, it's $other")
        None
    }
  }

  def dump() = {
    var ret = ""
    if (key_up) ret += "[key_up]"
    if (key_down) ret += "[key_down]"
    if (key_left) ret += "[key_left]"
    if (key_right) ret += "[key_right]"
    if (key_prev) ret += "[key_prev]"
    if (key_next) ret += "[key_next]"
    if (key_action) ret += "[key_action]"
    if (key_jump) ret += "[key_jump]"

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

  def setKey(key: String, down: Boolean): Boolean = {
    key match {
      case "W" | "w" | "ArrowUp" => key_up = down
      case "A" | "a" | "ArrowLeft" => key_left = down
      case "S" | "s" | "ArrowDown" => key_down = down
      case "D" | "d" | "ArrowRight" => key_right = down
      case "Q" | "q" => key_prev = down
      case "E" | "e" => key_next = down
      case " " => key_jump = down
      case _ => return false
    }
    dump()
    return true
  }

  var mouse_x = 0d
  var mouse_y = 0d
  var last_wheel_event = -1d

  def input_init() = {
    println("Input constructor")

    document.onkeydown = (ev: KeyboardEvent) => {
      if (setKey(ev.key, true)) {
        ev.preventDefault()
      }
    }
    document.onkeyup = (ev: KeyboardEvent) => {
      if (setKey(ev.key, false)) {
        ev.preventDefault()
      }
    }
    document.onwheel = (ev) => {
      // Allow for one wheel event every 0.1s. This sucks, but prevents free
      // spinning or touch scrolling mouses (eg. Apple Magic Mouse) from doing
      // wild things.
      if (game_time - last_wheel_event > 0.1) {

        if (ev.deltaY > 1) {
          key_next = true
        } else {
          key_prev = true
        }
   //     last_wheel_event = game_time
        dump()

      }
    }

    val c = getCanvas("c").get

    c.onmousemove = (ev: MouseEvent) => {
      mouse_x += ev.movementX
      mouse_y += ev.movementY
      dump()
    }
    c.onmousedown = (ev: MouseEvent) => {
      ev.preventDefault()
      ev.button match {
        case 0 => key_action = true
        case 2 => key_jump = true
      }
      dump()
    }
    c.onmouseup = (ev: MouseEvent) => {
      ev.preventDefault()
      ev.button match {
        case 0 => key_action = false
        case 2 => key_jump = false
      }
      dump()

    }

  }
}