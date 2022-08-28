package com.publicscript.qucore

import org.scalajs.dom.html.Canvas
import org.scalajs.dom.{KeyboardEvent, MouseEvent, document}

class InputLocal extends Input {

  private def getCanvas(id: String): Option[Canvas] = {
    val queryResult = document.querySelector(s"#$id")
    queryResult match {
      case canvas: Canvas => Some(canvas)
      case other =>
        println(s"Element with ID $id is not an canvas, it's $other")
        None
    }
  }

  private def init() = {

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
      if (Game.world.time - last_wheel_event > 0.1) {

        if (ev.deltaY > 1) {
          key_next = true
        } else {
          key_prev = true
        }
      }
    }

    val c = getCanvas("c").get

    c.onmousemove = (ev: MouseEvent) => {
      mouse_x += ev.movementX
      mouse_y += ev.movementY
    }
    c.onmousedown = (ev: MouseEvent) => {
      ev.preventDefault()
      ev.button match {
        case 0 => key_action = true
        case 2 => key_jump = true
      }
    }
    c.onmouseup = (ev: MouseEvent) => {
      ev.preventDefault()
      ev.button match {
        case 0 => key_action = false
        case 2 => key_jump = false
      }
    }

  }

  init()
}