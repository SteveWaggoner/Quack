package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_pickup_nailgun,sfx_pickup}
import com.publicscript.qucore.Game.{game_entity_player,audio}

class EntityPickupNailgun(apos:Vec3) extends EntityPickup(apos) {

  this.texture = Some(12)
  this.model = Some(model_pickup_nailgun)

  override def update() = {
    this.yaw += 0.02
    super.update()
  }

  def pickup() = {
    audio.play(sfx_pickup)
    game_entity_player.weapons.addOne(new WeaponNailgun())
    game_entity_player.weapon_index = game_entity_player.weapons.length - 1
    this.kill()
  }

}
