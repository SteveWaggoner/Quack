package com.publicscript.qucore

import org.scalajs.dom.{HTMLCanvasElement, document}

import scala.scalajs.js.{Any, Array}

object TTT {

  def rgba_from_2byte(c: Int):String = {
    "rgba(" + Array(
      ((c >> 12) & 15) * 17,
      ((c >> 8) & 15) * 17,
      ((c >> 4) & 15) * 17,
      (c & 15).toDouble / 15
    ).join() + ")"
  }


  def ttt(td: Array[Array[Any]], only_this_index: Int = -1, stack_depth: Int = 0): Array[HTMLCanvasElement] = {

    val sel_td = if (only_this_index >= 0) td.slice(only_this_index, only_this_index + 1) else td

    var tex_i=0

    sel_td.map((d: Array[Any]) => {

      var i = 0
      val e = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
      val c = e.getContext("2d")

      def fill_rect(x: Int, y: Int, w: Int, h: Int, topcolor: Int, bottomcolor: Int, fillcolor: Int) = {

        c.fillStyle = rgba_from_2byte(topcolor)
        c.fillRect(x-1, y-1, w, h)

        c.fillStyle = rgba_from_2byte(bottomcolor)
        c.fillRect(x+1, y+1, w, h)

        c.fillStyle = rgba_from_2byte(fillcolor)
        c.fillRect(x, y, w, h)
      }

      def action_rectangle(d: Array[Any], i: Int): Int = {
        // 0 - rectangle: x, y, width, height, top, bottom, fill
        val x = d(i + 0).asInstanceOf[Double].toInt
        val y = d(i + 1).asInstanceOf[Double].toInt
        val width = d(i + 2).asInstanceOf[Double].toInt
        val height = d(i + 3).asInstanceOf[Double].toInt
        val top = d(i + 4).asInstanceOf[Double].toInt
        val bottom = d(i + 5).asInstanceOf[Double].toInt
        val fill = d(i + 6).asInstanceOf[Double].toInt

        fill_rect(x, y, width, height, top, bottom, fill)
        return 7
      }

      def action_rectangle_multiple(d: Array[Any], i: Int): Int = {
        // 1 - rectangle_multiple: start_x, start_y, width, height,
        //                         inc_x, inc_y, top, bottom, fill
        val sx = d(i + 0).asInstanceOf[Double].toInt
        val sy = d(i + 1).asInstanceOf[Double].toInt
        val width = d(i + 2).asInstanceOf[Double].toInt
        val height = d(i + 3).asInstanceOf[Double].toInt
        val inc_x = d(i + 4).asInstanceOf[Double].toInt
        val inc_y = d(i + 5).asInstanceOf[Double].toInt
        val top = d(i + 6).asInstanceOf[Double].toInt
        val bottom = d(i + 7).asInstanceOf[Double].toInt
        val fill = d(i + 8).asInstanceOf[Double].toInt
        for (x <- sx until e.width by inc_x) {
          for (y <- sy until e.height by inc_y) {
            fill_rect(x, y, width, height, top, bottom, fill)
          }
        }

        return 9
      }

      def action_random_noise(d: Array[Any], i: Int): Int = {
        // 2 - random noise: color, size
        val color = d(i + 0).asInstanceOf[Double].toInt
        val size = d(i + 1).asInstanceOf[Double].toInt

        for (x <- 0 until e.width by size) {
          for (y <- 0 until e.height by size) {
            // Take the color value (first 3 nibbles) and
            // randomize the alpha value (last nibble)
            // between 0 and the input alpha.
            fill_rect(x, y, size, size, 0, 0, ((color & 0xfff0) + ( scala.scalajs.js.Math.random() * (color & 15) ) + 0.8).toInt)  //SLW: roundup make it have less black spots
          }
        }
        return 2
      }

      def action_text(d: Array[Any], i: Int): Int = {
        // 3 - text: x, y, color, font,size, text
        val x = d(i + 0).asInstanceOf[Double].toInt
        val y = d(i + 1).asInstanceOf[Double].toInt
        val color = d(i + 2).asInstanceOf[Double].toInt
        val font = d(i + 3).asInstanceOf[Double].toInt
        val size = d(i + 4).asInstanceOf[Double].toInt
        val text = d(i + 5).asInstanceOf[String]

        c.fillStyle = rgba_from_2byte(color)
        c.font = size + "px " + Array("sans-", "")(font) + "serif"
        c.fillText(text, x, y)
        return 6
      }

      def action_draw_texture(d: Array[Any], i: Int, stack_depth: Int): Int = {
        // 4 - draw a previous texture
        // We limit the stack depth here to not end up in an infinite
        // loop by accident
        val texture_index = d(i + 0).asInstanceOf[Double].toInt
        val x = d(i + 1).asInstanceOf[Double].toInt
        val y = d(i + 2).asInstanceOf[Double].toInt
        val width = d(i + 3).asInstanceOf[Double].toInt
        val height = d(i + 4).asInstanceOf[Double].toInt
        val alpha = d(i + 5).asInstanceOf[Double]

        c.globalAlpha = alpha / 15
        if ( texture_index < td.length && stack_depth < 16 ) {
          c.drawImage(ttt(td, texture_index, stack_depth + 1)(0), x, y, width, height)
        }
        c.globalAlpha = 1
        return 6
      }

      // Set up canvas width and height
      e.width = d(i + 0).asInstanceOf[Double].toInt
      e.height = d(i + 1).asInstanceOf[Double].toInt

      // Fill with background color
      fill_rect(0, 0, e.width, e.height, 0, 0, d(i + 2).asInstanceOf[Double].toInt)
      i += 3

      // Perform all the steps for this texture
      while (i < d.length) {

        val action = d(i).asInstanceOf[Double].toInt
        i += 1

        action match {
          case 0 => i += action_rectangle(d, i)
          case 1 => i += action_rectangle_multiple(d, i)
          case 2 => i += action_random_noise(d, i)
          case 3 => i += action_text(d, i)
          case 4 => i += action_draw_texture(d, i, stack_depth)
          case _ => throw new IllegalArgumentException(s"unknown action: $action")
        }

      }

      tex_i = tex_i + 1

      e
    })

  }
}

