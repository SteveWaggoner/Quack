package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{sfx_pickup}
import com.publicscript.qucore.Game.{game_entity_player,audio}

class EntityPickupHealth(apos:Vec3) extends EntityPickup(apos) {

  this.texture = Some(23)

  def pickup() = {
    audio.play(sfx_pickup)
    game_entity_player.health += 25
    this.kill()
  }

}

