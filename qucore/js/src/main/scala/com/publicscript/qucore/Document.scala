package com.publicscript.qucore

import org.scalajs.dom.{HTMLCanvasElement, HTMLDivElement, HTMLInputElement, document}

object Document {

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
