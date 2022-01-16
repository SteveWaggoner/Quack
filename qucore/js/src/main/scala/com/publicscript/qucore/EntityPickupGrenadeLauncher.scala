package com.publicscript.qucore

import com.publicscript.qucore.Main.{model_pickup_grenadelauncher, sfx_pickup}
import com.publicscript.qucore.Audio.audio_play
import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Game.game_entity_player


class EntityPickupGrenadeLauncher(apos:Vec3) extends EntityPickup(apos) {

    this.texture = Some(21)
    this.model = Some(model_pickup_grenadelauncher)

  override def update() = {
    this.yaw += 0.02
    super.update()
  }

  def pickup() = {
    audio_play(sfx_pickup)
    game_entity_player.weapons.addOne(new WeaponGrenadeLauncher())
    game_entity_player.weapon_index =  game_entity_player.weapons.length-1
    this.kill()
  }

}

