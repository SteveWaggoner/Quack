package com.publicscript.qucore

import com.publicscript.qucore.Resources.{model_pickup_grenadelauncher, sfx_pickup}
import com.publicscript.qucore.MathUtils.Vec3

class EntityPickupGrenadeLauncher(world:World, apos:Vec3) extends EntityPickup(world, apos) {

    this.texture = Some(21)
    this.model = Some(model_pickup_grenadelauncher)

  override def update() = {
    this.yaw += 0.02
    super.update()
  }

  def pickup() = {
    world.audio_play(sfx_pickup)
    world.player.weapons.addOne(new WeaponGrenadeLauncher(world))
    world.player.weapon_index =  world.player.weapons.length-1
    this.kill()
  }

}

