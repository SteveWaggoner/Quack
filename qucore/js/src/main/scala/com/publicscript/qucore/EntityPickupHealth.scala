package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{sfx_pickup}
import com.publicscript.qucore.Game.{audio}

class EntityPickupHealth(world:World, apos:Vec3) extends EntityPickup(world, apos) {

  this.texture = Some(23)

  def pickup() = {
    audio.play(sfx_pickup)
    world.player.health += 25
    this.kill()
  }

}

