package com.publicscript.qucore

class Clock extends Serializable {

  var frames = 0
  var time = 0.016d
  var tick = 0d

  private var real_time_last = 0d


  def set_time_now(time_now_par:Double) = {
    var time_now = time_now_par

    time_now *= 0.001

    if (real_time_last == 0)
      real_time_last = time_now

    tick = Math.min(time_now - real_time_last, 0.05)
    real_time_last = time_now
    time += tick

    frames = frames + 1
  }

  def fps() : Double = {
    frames / time
  }


  override def writeState(state: State): Unit = {
    state.writeInt(frames)
    state.writeFloat(time)
    state.writeFloat(tick)
  }

  override def readState(state: State): Unit = {
    frames = state.readInt()
    time = state.readFloat()
    tick = state.readFloat()
  }

}
