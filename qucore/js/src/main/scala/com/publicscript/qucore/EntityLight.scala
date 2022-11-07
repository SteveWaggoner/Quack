package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,scale}


class EntityLight(world:World, pos:Vec3, var light:Double, colorNum:Int) extends Entity(world, pos) {

  val spawn_time = world.time

  val flicker = if (light == 1) true else false

  val color = Array(
    (colorNum & 0x7) << 5,
    (colorNum & 0x1c) << 3,
    colorNum & 0xc0
  )


  override def update() = {
    if (this.flicker && Math.random() > 0.9) {
      this.light = if (Math.random() > 0.5) 10 else 0
    }
    var intensity = this.light
    // If this light is a temporary one, fade it out over its lifetime
    if (this.die_at != 0) {
      if (this.die_at < world.time) {
        this.kill()
      }
      intensity = scale(world.time, this.spawn_time, this.die_at, 1, 0) * this.light
    }
    world.display.render.push_light(this.pos, intensity, this.color(0), this.color(1), this.color(2))
  }

}

