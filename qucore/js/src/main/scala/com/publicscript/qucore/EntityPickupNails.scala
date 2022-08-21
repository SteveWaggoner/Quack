package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{sfx_pickup}
import com.publicscript.qucore.Game.{audio}

class EntityPickupNails(world:World, apos:Vec3) extends EntityPickup(world, apos) {

  this.texture = Some(24)

  def pickup() = {
    for (w <- world.player.weapons) {
      if (w.isInstanceOf[WeaponNailgun]) {
        w.ammo += 50
        audio.play(sfx_pickup)
        this.kill()
      }
    }
  }

}

