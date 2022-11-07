package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3}

import scala.scalajs.js.typedarray.{Float32Array, Uint32Array, Uint8Array}

class State {
  private var intArrLen = 0
  private var intArrPos = 0
  private val intArr = new Uint32Array(160000)

  private var byteArrLen = 0
  private var byteArrPos = 0
  private val byteArr = new Uint8Array(160000)

  private var floatArrLen = 0
  private var floatArrPos = 0
  private val floatArr = new Float32Array(160000)

  def reset() = {
    intArrPos = 0
    byteArrPos = 0
    floatArrPos = 0
  }

  def writeByte(b:Short) = {
    byteArr(byteArrLen) = b
    byteArrLen = byteArrLen + 1
  }

  def readByte() : Short = {
    byteArrPos = byteArrPos + 1
    byteArr(byteArrPos-1)
  }

  def writeBool(b:Boolean) = {
    writeByte(if ( b ) 1 else 0)
  }

  def readBool() : Boolean = {
    if ( readByte() != 0 ) true else false
  }

  def writeInt(i:Int) = {
    intArr(byteArrLen) = i
    intArrLen = intArrLen + 1
  }

  def readInt() : Int = {
    intArrPos = intArrPos + 1
    intArr(intArrPos-1).toInt
  }

  def writeFloat(n:Double) = {
    floatArr(floatArrLen) = n.toFloat
    floatArrLen = floatArrLen + 1
  }

  def readFloat() : Float = {
    floatArrPos = floatArrPos + 1
    floatArr(floatArrPos-1)
  }

  def writeVec3(vec3:Vec3) = {
    writeFloat(vec3.x)
    writeFloat(vec3.y)
    writeFloat(vec3.z)
  }

  def readVec3(vec3:Vec3) = {
    vec3.x = readFloat()
    vec3.y = readFloat()
    vec3.z = readFloat()
  }

}
