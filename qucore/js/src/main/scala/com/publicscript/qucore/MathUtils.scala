package com.publicscript.qucore

object MathUtils {

  import scala.scalajs.js.Math

  def clamp(v: Double, min: Double, max: Double) : Double = {
    if (v < min) min else if (v > max) max else v
  }

  def scale(v: Double, in_min: Double, in_max: Double, out_min: Double, out_max: Double) : Double = {
    out_min + (out_max - out_min) * (v - in_min) / (in_max - in_min)
  }

  def anglemod(r: Double) : Double = {
    Math.atan2(Math.sin(r), Math.cos(r))
  }

  case class Vec3(var x: Double, var y: Double, var z: Double);


  def vec3(x: Double = 0, y: Double = 0, z: Double = 0) = {
    new Vec3(x, y, z)
  }

  def vec3_rotate_yaw_pitch(p: Vec3, yaw: Double, pitch: Double) = {
    vec3_rotate_y(vec3_rotate_x(p, pitch), yaw)
  }

  def vec3_rotate_y(p: Vec3, rad: Double) = {
    vec3(p.z * Math.sin(rad) + p.x * Math.cos(rad), p.y, p.z * Math.cos(rad) - p.x * Math.sin(rad))
  }

  def vec3_rotate_x(p: Vec3, rad: Double) = {
    vec3(p.x, p.y * Math.cos(rad) - p.z * Math.sin(rad), p.y * Math.sin(rad) + p.z * Math.cos(rad))
  }

  def vec3_2d_angle(a: Vec3, b: Vec3) = {
    Math.atan2(b.x - a.x, b.z - a.z)
  }

  def vec3_clone(a: Vec3) = {
    vec3(a.x, a.y, a.z)
  }

  def vec3_length(a: Vec3) = {
    Math.hypot(a.x, a.y, a.z)
  }

  def vec3_dist(a: Vec3, b: Vec3) = {
    vec3_length(vec3_sub(a, b))
  }

  def vec3_dot(a: Vec3, b: Vec3) = {
    a.x * b.x + a.y * b.y + a.z * b.z
  }

  def vec3_add(a: Vec3, b: Vec3) = {
    vec3(a.x + b.x, a.y + b.y, a.z + b.z)
  }

  def vec3_sub(a: Vec3, b: Vec3) = {
    vec3(a.x - b.x, a.y - b.y, a.z - b.z)
  }

  def vec3_mul(a: Vec3, b: Vec3) = {
    vec3(a.x * b.x, a.y * b.y, a.z * b.z)
  }

  def vec3_mulf(a: Vec3, b: Double) = {
    vec3(a.x * b, a.y * b, a.z * b)
  }

  def vec3_cross(a: Vec3, b: Vec3) = {
    vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
  }

  def vec3_normalize(v: Vec3) = {
    vec3_mulf(v, 1 / vec3_length(v))
  }

  def vec3_face_normal(v0: Vec3, v1: Vec3, v2: Vec3) = {
    vec3_normalize(vec3_cross(vec3_sub(v0, v1), vec3_sub(v2, v1)))
  }
}
