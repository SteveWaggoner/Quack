package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{sfx_pickup}

class EntityPickupNails(world:World, pos:Vec3) extends EntityPickup(world, pos) {

  this.texture = Some(24)

  def pickup() = {
    for (w <- world.player.weapons) {
      if (w.isInstanceOf[WeaponNailgun]) {
        w.ammo += 50
        world.play_sound(sfx_pickup)
        this.kill()
      }
    }
  }

}

