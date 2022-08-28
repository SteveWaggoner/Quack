package com.publicscript.qucore

trait Serializable {

  def writeState(state: State)
  def readState(state: State)

}
