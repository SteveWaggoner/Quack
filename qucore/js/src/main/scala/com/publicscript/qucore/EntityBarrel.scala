package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_dist,scale,vec3_add}
import com.publicscript.qucore.Resources.{model_barrel,sfx_grenade_explode,model_gib_pieces}

class EntityBarrel(world:World, pos:Vec3) extends Entity(world, pos) {

  this.model = Some(model_barrel)
  this.texture = Some(21)
  this.pitch = Math.PI / 2
  this.health = 10
  this.size = vec3(8, 32, 8)
  this.is_enemy = true

  override def update() = {
    draw_model()
  }

  override def kill() = {

    // Deal some damage to nearby entities
    for (entity <- world.get_entity_group(Entity.ENTITY_GROUP_ENEMY)) {
      val dist = vec3_dist(this.pos, entity.pos);
      if (entity != this && dist < 256) {
        entity.receive_damage(this, scale(dist, 0, 256, 60, 0))
      }
    }
    super.kill()
    this.play_sound(sfx_grenade_explode)
    for (m <- model_gib_pieces) {
      this.spawn_particles(2, 600, m, 21, 1)
    }
    world.spawn("light", vec3_add(this.pos, vec3(0, 16, 0)), 250, 0x08f, lifetime=0.2)
   }

}
