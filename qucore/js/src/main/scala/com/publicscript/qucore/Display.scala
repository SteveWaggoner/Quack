package com.publicscript.qucore

import scala.scalajs.js.timers.{SetTimeoutHandle, clearTimeout, setTimeout}

import org.scalajs.dom.{HTMLCanvasElement, HTMLDivElement, HTMLInputElement, document}

object Display {

  val c : HTMLCanvasElement = document.querySelector("#c").asInstanceOf[HTMLCanvasElement]
  val ts : HTMLDivElement = document.querySelector("#ts").asInstanceOf[HTMLDivElement]
  val m : HTMLInputElement = document.querySelector("#m").asInstanceOf[HTMLInputElement]
  val mi : HTMLInputElement = document.querySelector("#mi").asInstanceOf[HTMLInputElement]
  val f : HTMLInputElement = document.querySelector("#f").asInstanceOf[HTMLInputElement]

  val g:HTMLDivElement = document.querySelector("#g").asInstanceOf[HTMLDivElement]

  val a:HTMLDivElement = document.querySelector("#a").asInstanceOf[HTMLDivElement]
  val h:HTMLDivElement = document.querySelector("#h").asInstanceOf[HTMLDivElement]
  val msg:HTMLDivElement = document.querySelector("#msg").asInstanceOf[HTMLDivElement]

  val fps:HTMLDivElement = document.querySelector("#fps").asInstanceOf[HTMLDivElement]

}


class Display {

  private var message_timeout : SetTimeoutHandle = _
  val render = new Render(Display.c)

  def set_title_message(msg: String, sub: String = "") = {

    if ( msg == "" && sub == "" ) {
      Display.ts.style.display = "none"
    } else {
      Display.ts.innerHTML = "<h1>" + msg + "</h1>" + sub
      Display.ts.style.display = "block"
    }
  }
  def show_game_message(text: String) = {
    Display.msg.textContent = text
    Display.msg.style.display = "block"
    clearTimeout(message_timeout)
    message_timeout = setTimeout(2000) { Display.msg.style.display = "none" }
  }

  def set_health(health: String) = {
    Display.h.textContent = health
  }

  def set_ammo(ammo: String) = {
    Display.a.textContent = ammo
  }

  def set_fps(fps:Double) = {
    Display.fps.textContent = "FPS: " + fps
  }

  def get_mouse_sensitivity(): Double = {
    Display.m.value.toDouble
  }

  def get_mouse_inverted() : Boolean = {
    Display.mi.checked
  }

}
