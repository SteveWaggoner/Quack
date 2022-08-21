package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.Vec3
import com.publicscript.qucore.Resources.{model_pickup_nailgun,sfx_pickup}
import com.publicscript.qucore.Game.{audio}

class EntityPickupNailgun(world:World, apos:Vec3) extends EntityPickup(world, apos) {

  this.texture = Some(12)
  this.model = Some(model_pickup_nailgun)

  override def update() = {
    this.yaw += 0.02
    super.update()
  }

  def pickup() = {
    audio.play(sfx_pickup)
    world.player.weapons.addOne(new WeaponNailgun())
    world.player.weapon_index = world.player.weapons.length - 1
    this.kill()
  }

}
