package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_pickup_nailgun,sfx_pickup}

class EntityPickupNailgun(world:World, apos:Vec3) extends EntityPickup(world, apos) {

  this.texture = Some(12)
  this.model = Some(model_pickup_nailgun)

  override def update() = {
    this.yaw += 0.02
    super.update()
  }

  def pickup() = {
    world.audio_play(sfx_pickup)
    world.player.weapons.addOne(new ItemWeaponNailgun(world))
    world.player.weapon_index = world.player.weapons.length - 1
    this.kill()
  }

}
