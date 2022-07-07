package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3_add,vec3}
import com.publicscript.qucore.Resources.{model_nail,model_explosion,sfx_nailgun_hit}
import com.publicscript.qucore.Game.{game_time,game_spawn}
import com.publicscript.qucore.Render.r_push_light

class EntityProjectilePlasma(apos:Vec3) extends Entity(apos) {

    this.texture = Some(21)
    this.model = Some(model_nail)
    this.gravity = 0
    this.die_at = game_time + 3

  override def update():Unit = {
    this.update_physics()
    this.draw_model()
    r_push_light(this.pos, 5, 255, 128, 0)
  }

  override def did_collide(axis: Int):Unit = {
    this.kill()
    this.play_sound(sfx_nailgun_hit)
    this.spawn_particles(2, 80, model_explosion, 8, 0.4)
    game_spawn("light", vec3_add(this.pos, vec3(0, 10, 0)), 5, 0xf5).die_at = game_time + 0.1
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    this.kill()
    other.receive_damage(this, 15)
  }

}

