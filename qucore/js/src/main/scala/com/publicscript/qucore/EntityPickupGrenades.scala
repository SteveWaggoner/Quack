package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_pickup_grenades, sfx_pickup}


class EntityPickupGrenades(world:World, apos:Vec3) extends EntityPickup(world, apos) {

  this.texture = Some(25)
  this.model = Some(model_pickup_grenades)

  def pickup() = {
    for (w <- world.player.weapons) {
      if (w.isInstanceOf[WeaponGrenadeLauncher]) {
        w.ammo += 10
        world.audio_play(sfx_pickup)
        this.kill()
      }
    }
  }

}

