package com.publicscript.qucore

import org.scalajs.dom.{HTMLCanvasElement, HTMLDivElement, HTMLInputElement, document}

import scala.scalajs.js.{Any, Array}

object TTT {

  def ttt(td: Array[Array[Any]], only_this_index: Int = -1, stack_depth: Int = 0):Unit = {

    val sel_td = if ( only_this_index >= 0) td.slice(only_this_index, only_this_index+1) else td

    sel_td.map((d: Array[Any]) => {

      var i = 0
      val e = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
      val c = e.getContext("2d")

      def rgba_from_2byte(c: Int) = {
        "rgba(" + Array(
          ((c >> 12) & 15) * 17,
          ((c >> 8) & 15) * 17,
          ((c >> 4) & 15) * 17,
          (c & 15) / 15
        ).join() + ")"
      }

      def fill_rect(x: Int, y: Int, w: Int, h: Int, topcolor:Int, bottomcolor:Int, fillcolor:Int) = {

          c.fillStyle = rgba_from_2byte(fillcolor)
          c.fillRect(x , y, w, h)
      }
      // Set up canvas width and height
      e.width = d(i+0).asInstanceOf[Double].toInt
      e.height = d(i+1).asInstanceOf[Double].toInt

      // Fill with background color
      fill_rect(0, 0, e.width, e.height, 0, 0, d(i+2).asInstanceOf[Double].toInt)
      i += 3

      // Perform all the steps for this texture
      while (i < d.length) {
        val f = Array(
          // 0 - rectangle: x, y, width, height, top, bottom, fill
          (x: Double, y: Double, width, height, top: Double, bottom, fill) => {
            fill_rect(x, y, width, height, top, bottom, fill)
          }
          ,
          // 1 - rectangle_multiple: start_x, start_y, width, height,
          //                         inc_x, inc_y, top, bottom, fill
          (sx: Double, sy: Double, w, h, inc_x: Double, inc_y: Double, top: Double, bottom, fill) => {
            for (x <- sx until e.width by inc_x) {
              for (y <- sy until e.height by inc_y) {
                fill_rect(x, y, w, h, top, bottom, fill)
              }
            }
          }
          ,
          // 2 - random noise: color, size
          (color: Double, size: Double) => {
            for (x <- 0 until e.width by size) {
              for (y <- 0 until e.height by size) {
                // Take the color value (first 3 nibbles) and
                // randomize the alpha value (last nibble)
                // between 0 and the input alpha.
                fill_rect(x, y, size, size, 0, 0, (color & 0xfff0) + Math.random() * (color & 15))
              }
            }
          }
          ,
          // 3 - text: x, y, color, font,size, text
          (x, y, color: Double, font, size, text) => {
            c.fillStyle = rgba_from_2byte(color)
            c.font = size + "px " + Array("sans-", "")(font) + "serif"
            c.fillText(text, x, y)
          }
          ,
          // 4 - draw a previous texture
          // We limit the stack depth here to not end up in an infinite
          // loop by accident
          (texture_index: Double, x, y, w, h, alpha: Double) => {
            c.globalAlpha = alpha / 15
            texture_index < td.length && stack_depth < 16 && c.drawImage(ttt(td, texture_index, stack_depth + 1)(0), x, y, w, h)
            c.globalAlpha = 1
          }
        )(d({
          val temp = i
          i += 1
          temp
        }))
        f(/* Unsupported: SpreadElement */ ...d.slice(i, i+=f.length)
        )
      }
      return e
    }
    )
  }

}
