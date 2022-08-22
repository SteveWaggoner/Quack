package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3,vec3,vec3_add,vec3_dist,scale}
import com.publicscript.qucore.Resources.{model_explosion,model_grenade,sfx_grenade_bounce,sfx_grenade_explode}

class EntityProjectileGrenade(world:World, apos:Vec3) extends Entity(world, apos) {


    this.texture = Some(8)
    this.model = Some(model_grenade)
    this.die_at = world.time + 2
    this.bounciness = 0.5
    val damage = 120


  override def update() = {
    super.update_physics()
    this.draw_model()
    world.render_light(vec3_add(this.pos, vec3(0, 16, 0)), (Math.sin(world.time * 10) + 2) * 0.5, 255, 32, 0)
    this.friction = if (this.on_ground) 5 else 0.5
  }

  override def did_collide(axis: Int):Unit = {
    if (axis != 1 || this.veloc.y < -128) {
      this.yaw += Math.random()
      this.play_sound(sfx_grenade_bounce)
    }
  }

  override def did_collide_with_entity(other: Entity):Unit = {
    this.kill()
  }

  override def kill() = {
    // Deal some damage to nearby entities
    for (entity <- this.check_entities) {
      val dist = vec3_dist(this.pos, entity.pos);
      if (dist < 196) {
        entity.receive_damage(this, scale(dist, 0, 196, this.damage, 0))
      }
    }
    super.kill()
    this.play_sound(sfx_grenade_explode)
    this.spawn_particles(20, 800, model_explosion, 8, 1)
    world.spawn("light", vec3_add(this.pos, vec3(0, 16, 0)), 250, 0x08f, lifetime=0.2)
  }

}


