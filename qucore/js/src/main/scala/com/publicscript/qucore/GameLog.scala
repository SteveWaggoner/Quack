package com.publicscript.qucore

class GameLog {

  val state=new State()

  def saveState(world:GameWorld) = {

    world.player.input.writeState(state)
    world.player.writeState(state)

/*
    var closestEnemy:Entity = null
    world.get_entity_group("enemy").foreach((e:Entity) => if ( closestEnemy == null || closestEnemy.get_distance_to_player() > e.get_distance_to_player()) { closestEnemy = e } )
    if ( closestEnemy != null)
      closestEnemy.writeState(state)
      */
  }

  def loadState(world:GameWorld) = {

    world.player.input.readState(state)
    world.player.readState(state)
/*
    var closestEnemy:Entity = null
    world.get_entity_group("enemy").foreach((e:Entity) => if ( closestEnemy == null || closestEnemy.get_distance_to_player() > e.get_distance_to_player()) { closestEnemy = e } )
    if ( closestEnemy != null)
      closestEnemy.readState(state)
      */
  }


}
