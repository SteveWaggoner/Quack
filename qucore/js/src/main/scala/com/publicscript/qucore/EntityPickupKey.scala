package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_pickup_key,sfx_pickup}

class EntityPickupKey(world:World, apos:Vec3) extends EntityPickup(world,apos) {


  this.texture = Some(21)
  this.model = Some(model_pickup_key)


  override def update() = {
    this.yaw += 0.02
    draw_model()
  }

  def pickup() = {
    world.audio_play(sfx_pickup)
    world.show_game_message("YOU GOT THE KEY!")

    world.no_entity_needs_key()

    this.kill()
  }

}

