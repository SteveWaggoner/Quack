package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{vec3}
import com.publicscript.qucore.Resources.{model_nailgun,sfx_nailgun_shoot}


class WeaponNailgun(world:World) extends Weapon(world) {
    this.texture = 4
    this.model = model_nailgun
    this.sound = sfx_nailgun_shoot
    this.ammo = 100
    this.reload = 0.09
    this.projectile_type = "nail"
    this.projectile_speed = 1300
    this.projectile_offset = vec3(6, 0, 8)

}


