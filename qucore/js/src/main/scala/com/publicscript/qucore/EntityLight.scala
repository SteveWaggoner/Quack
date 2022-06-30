package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,scale}
import com.publicscript.qucore.Render.r_push_light
import com.publicscript.qucore.Game.game_time


class EntityLight(apos:Vec3, alight:Double, acolor:Int) extends Entity(apos) {

    var light = alight
    val spawn_time = game_time

    val flicker = if (light == 1) true else false


    if (acolor==0) {
      println("no color!")
    }
    val color = Array(
      (acolor & 0x7) << 5,
      (acolor & 0x1c) << 3,
      acolor & 0xc0
    )


  override def update() = {
    if (this.flicker && Math.random() > 0.9) {
      this.light = if (Math.random() > 0.5) 10 else 0
    }
    var intensity = this.light
    // If this light is a temporary one, fade it out over its lifetime
    if (this.die_at!=0) {
      if (this.die_at < game_time) {
        this.kill()
      }
      intensity = scale(game_time, this.spawn_time, this.die_at, 1, 0) * this.light
    }
    r_push_light(this.pos, intensity, this.color(0), this.color(1), this.color(2))
  }

}

