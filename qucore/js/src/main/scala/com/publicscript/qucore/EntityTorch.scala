package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_add,vec3_mulf,vec3_sub}
import com.publicscript.qucore.Main.{model_torch}
import com.publicscript.qucore.Game.{game_time,game_spawn}
import com.publicscript.qucore.Render.r_push_light
import com.publicscript.qucore.Map.map_line_of_sight

class EntityTorch(apos:Vec3) extends Entity(apos) {

    this.texture = Some(30)
    this.model = Some(model_torch)
    this.anim = Anim(0.05, Array(0, 1, 2, 1, 2, 0, 0, 1, 2))
    this.pos.x -= 16
    this.pos.z -= 16
    var light_pos = this.pos
    // Find which wall we're on; move the torch model towards the wall and
    // the light position outwards
    var done = false
    for (trace_dir <- Array(vec3(-32,0,0), vec3(32,0,0), vec3(0,0,-32), vec3(0,0,32))) {
      if (!done) {
        val trace_end = vec3_add(this.pos, trace_dir)
        if (map_line_of_sight(this.pos, vec3_add(this.pos, trace_dir))) {
          this.pos = vec3_add(this.pos, vec3_mulf(trace_dir, 0.4))
          this.light_pos = vec3_sub(this.pos, vec3_mulf(trace_dir, 2))
          done = true //scala break
        }
      }
    }
    var light = 0d

  override def update() = {
    super.update()
    if (Math.random() > 0.8) {
      this.light = Math.random()
    }
    r_push_light(this.light_pos, Math.sin(game_time) + this.light + 6, 255, 192, 16)
  }

}


