package com.publicscript.qucore

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html.Button
import org.scalajs.dom.MouseEvent

object Main {

  def getButton(buttonId: String): Option[Button] = {
    val queryResult = document.querySelector(s"#$buttonId")
    queryResult match {
      case button: Button => Some(button)
      case other =>
        println(s"Element with ID $buttonId is not an image, it's $other")
        None
    }
  }

  def main(args: Array[String]): Unit = {
 //   val lib = new MyLibrary
 //   println(lib.sq(2))

    println(s"Using Scala.js version ${System.getProperty("java.vm.version")}")

    println("Let's yodel....")

    getButton("yodel").get.onclick = (e:MouseEvent) => {
      Audio.audio_init()
      Audio.audio_play_async(Audio.audio_load_url_async("https://s3-us-west-2.amazonaws.com/s.cdpn.io/123941/Yodel_Sound_Effect.mp3"))

    }

  }
}
