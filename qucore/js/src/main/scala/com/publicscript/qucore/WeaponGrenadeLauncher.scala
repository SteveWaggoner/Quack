package com.publicscript.qucore

import com.publicscript.qucore.Resources.{model_grenadelauncher,sfx_grenade_shoot}

class WeaponGrenadeLauncher(world:World) extends Weapon(world) {

    this.texture = 21
    this.model = model_grenadelauncher
    this.sound = sfx_grenade_shoot
    this.ammo = 10
    this.reload = 0.650
    this.projectile_type = "grenade"
    this.projectile_speed = 900

}


