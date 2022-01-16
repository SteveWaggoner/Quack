package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Main.{model_pickup_key,sfx_pickup}
import com.publicscript.qucore.Audio.{audio_play}

import com.publicscript.qucore.Game.{game_show_message,game_entities}

class EntityPickupKey(apos:Vec3) extends EntityPickup(apos) {


  this.texture = Some(21)
  this.model = Some(model_pickup_key)


  override def update() = {
    this.yaw += 0.02
    super.update()
  }

  def pickup() = {
    audio_play(sfx_pickup)
    game_show_message("YOU GOT THE KEY!")
    for (e <- game_entities) {
      if (e.needs_key) {
        e.needs_key = false
      }
    }
    this.kill()
  }

}

