package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_pickup_grenades, sfx_pickup}
import com.publicscript.qucore.Game.{game_entity_player}
import com.publicscript.qucore.Audio.{audio_play}


class EntityPickupGrenades(apos:Vec3) extends EntityPickup(apos) {

  this.texture = Some(25)
  this.model = Some(model_pickup_grenades)

  def pickup() = {
    for (w <- game_entity_player.weapons) {
      if (w.isInstanceOf[WeaponGrenadeLauncher]) {
        w.ammo += 10
        audio_play(sfx_pickup)
        this.kill()
      }
    }
  }

}

