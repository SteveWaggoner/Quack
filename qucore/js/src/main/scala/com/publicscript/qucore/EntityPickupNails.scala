package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Main.{sfx_pickup}
import com.publicscript.qucore.Audio.{audio_play}
import com.publicscript.qucore.Game.{game_entity_player}

class EntityPickupNails(apos:Vec3) extends EntityPickup(apos) {

  this.texture = Some(24)

  def pickup() = {
    for (w <- game_entity_player.weapons) {
      if (w.isInstanceOf[WeaponNailgun]) {
        w.ammo += 50
        audio_play(sfx_pickup)
        this.kill()
      }
    }
  }

}

