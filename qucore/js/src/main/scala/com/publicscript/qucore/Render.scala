package com.publicscript.qucore

import com.publicscript.qucore.MathUtils.{Vec3, clamp, scale, vec3, vec3_dist, vec3_face_normal}

import org.scalajs.dom.HTMLCanvasElement

import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.Any
import scala.collection.mutable.ArrayBuffer


class Render(val canvas: HTMLCanvasElement) {

  private val gl_options = js.Dynamic.literal(antialias = false)
  private val gl = canvas.getContext("webgl", gl_options) || canvas.getContext("experimental-webgl", gl_options)

  private val R_MAX_VERTS = 1024 * 64
  private val R_MAX_LIGHT_V3 = 64

  // Vertex shader source. This translates the model position & rotation and also
  // mixes positions of two buffers for animations.
  private val R_SOURCE_VS = "precision highp float;" +
    // Vertex positions, normals and uv coords for the fragment shader
    "varying vec3 vp,vn;" + "varying vec2 vt;" +
    // Input vertex positions & normals and blend vertex positions & normals
    "attribute vec3 p,n,p2,n2;" +
    // Input UV coords
    "attribute vec2 t;" +
    // Camera position (x, y, z) and aspect ratio (w)
    "uniform vec4 c;" +
    // Model position (x, y, z)
    "uniform vec3 mp;" +
    // Model rotation (yaw, pitch)
    "uniform vec2 mr;" +
    // Mouse rotation yaw (x), pitch (y)
    "uniform vec2 m;" +
    // Blend factor between the two vertex positions
    "uniform float f;" +
    // Generate a rotation Matrix around the x,y,z axis;
    // Used for model rotation and camera yaw
    "mat4 rx(float r){" +
        "return mat4(" +
            "1,0,0,0," +
            "0,cos(r),sin(r),0," +
            "0,-sin(r),cos(r),0," +
            "0,0,0,1" +
            ");" +
    "}" +
    "mat4 ry(float r){" +
        "return mat4(" +
            "cos(r),0,-sin(r),0," +
            "0,1,0,0," +
            "sin(r),0,cos(r),0," +
            "0,0,0,1" +
            ");" +
        "}" +
    "mat4 rz(float r){" +
        "return mat4(" +
            "cos(r),sin(r),0,0," +
            "-sin(r),cos(r),0,0," +
            "0,0,1,0," +
            "0,0,0,1" +
            ");" +
        "}" +
    "void main(void){" +
        // Rotation Matrixes for model rotation
        "mat4 " + "mry=ry(mr.x)," + "mrz=rz(mr.y);" +
        // Mix vertex positions, rotate and add the model position
        "vp=(mry*mrz*vec4(mix(p,p2,f),1.)).xyz+mp;" +
        // Mix normals
        "vn=(mry*mrz*vec4(mix(n,n2,f),1.)).xyz;" +
        // UV coords are handed over to the fragment shader as is
        "vt=t;" +
        // Final vertex position is transformed by the projection matrix,
        // rotated around mouse yaw/pitch and offset by the camera position
        // We use a FOV of 90, so the matrix[0] and [5] are conveniently 1.
        // (1 / Math.tan((90/180) * Math.PI / 2) === 1)
        "gl_Position=" +
            "mat4(" +
                "1,0,0,0," +
                "0,c.w,0,0," +
                "0,0,1,1," +
                "0,0,-2,0" +
            ")*" +  // projection
            "rx(-m.y)*ry(-m.x)*" +
            "vec4(vp-c.xyz,1.);" +
    "}"

  private val R_SOURCE_FS = "precision highp float;" +
    // Vertex positions, normals and uv coords
    "varying vec3 vp,vn;" +
    "varying vec2 vt;" +
    "uniform sampler2D s;" +
    // Lights [(x,y,z), [r,g,b], ...]
    "uniform vec3 l[" + R_MAX_LIGHT_V3 + "];" +
    "void main(void){" +
        "gl_FragColor=texture2D(s,vt);" +
        // Debug: no textures
        //  "gl_FragColor=vec4(1.0,1.0,1.0,1.0);" +
        // Calculate all lights
        "vec3 vl;" +
        "for(int i=0;i<" + R_MAX_LIGHT_V3 + ";i+=2) {" +
            "vl+=" +
            // Angle to normal
              "max(" + "dot(" + "vn, normalize(l[i]-vp)" + ")" + ",0.)*" +
                  "(1./pow(length(l[i]-vp),2.))" +  // Inverse distance squared
                  "*l[i+1];" +  // Light color/intensity
        "}" +
        // Debug: full bright lights
        //  "vl = vec3(2,2,2);" +
        "gl_FragColor.rgb=floor(" +
            "gl_FragColor.rgb*pow(vl,vec3(0.75))" +  // Light, Gamma
            "*16.0+0.5" +
            ")/16.0;" +  // Reduce final output color for some extra dirty looks
    "}"

  private val r_buffer = new Float32Array(R_MAX_VERTS * 8)
  private var r_num_verts = 0
  private val r_light_buffer = new Float32Array(R_MAX_LIGHT_V3 * 3)
  private var r_num_lights = 0
  private val r_textures = new ArrayBuffer[Texture]

  var camera = vec3(0, 0, -50)
  var camera_pitch = 0.2d
  var camera_yaw = 0d

  private var r_draw_calls = ArrayBuffer.empty[DrawCall]


  private var r_u_camera : Any = null
  private var r_u_lights  : Any = null
  private var r_u_mouse  : Any = null
  private var r_u_pos  : Any = null
  private var r_u_rotation  : Any = null
  private var r_u_frame_mix  : Any = null

  private var r_va_p2: Any = null
  private var r_va_n2:Any = null

  r_init()

  private def r_init() : Unit = {

    val shader_program = gl.createProgram()
    gl.attachShader(shader_program, r_compile_shader(gl.VERTEX_SHADER, R_SOURCE_VS))
    gl.attachShader(shader_program, r_compile_shader(gl.FRAGMENT_SHADER, R_SOURCE_FS))
    gl.linkProgram(shader_program)
    gl.useProgram(shader_program)
    r_u_camera = gl.getUniformLocation(shader_program, "c")
    r_u_lights = gl.getUniformLocation(shader_program, "l")
    r_u_mouse = gl.getUniformLocation(shader_program, "m")
    r_u_pos = gl.getUniformLocation(shader_program, "mp")
    r_u_rotation = gl.getUniformLocation(shader_program, "mr")
    r_u_frame_mix = gl.getUniformLocation(shader_program, "f")
    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer())
    r_vertex_attrib(shader_program, "p", 3, 8, 0) // position
    r_vertex_attrib(shader_program, "t", 2, 8, 3) // texture coord
    r_vertex_attrib(shader_program, "n", 3, 8, 5)
    r_va_p2 = r_vertex_attrib(shader_program, "p2", 3, 8, 0)
    r_va_n2 = r_vertex_attrib(shader_program, "n2", 3, 8, 5) // mix normals
    gl.enable(gl.DEPTH_TEST)
    gl.enable(gl.BLEND)
    gl.enable(gl.CULL_FACE)
    gl.viewport(0, 0, canvas.width, canvas.height)
  }

  private def r_compile_shader(shader_type: Any, shader_source: String) = {

    val shader = gl.createShader(shader_type)
    gl.shaderSource(shader, shader_source)
    val res = gl.compileShader(shader)

    shader
  }

  private def r_vertex_attrib(shader_program: Any, attrib_name: String, count: Int, vertex_size: Int, offset: Int) = {
    val location = gl.getAttribLocation(shader_program, attrib_name)
    gl.enableVertexAttribArray(location)
    gl.vertexAttribPointer(location, count, gl.FLOAT, false, vertex_size * 4, offset * 4)
    location
  }

  case class Texture(gl_texture:scala.scalajs.js.Dynamic, canvas:HTMLCanvasElement)

  def create_texture(c: HTMLCanvasElement) = {

    val t = new Texture(gl_texture = gl.createTexture(), canvas = c)
    gl.bindTexture(gl.TEXTURE_2D, t.gl_texture)
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, c)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST_MIPMAP_NEAREST)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.REPEAT)
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.REPEAT)
    gl.generateMipmap(gl.TEXTURE_2D)

    r_textures.addOne(t)
  }

  def prepare_frame(r: Double, g: Double, b: Double) = {
    gl.clearColor(r, g, b, 1)
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT)
    r_num_lights = 0
    r_light_buffer.fill(0)
  }

  def end_frame() = {
    gl.uniform4f(r_u_camera, camera.x, camera.y, camera.z, 16.0 / 9)
    gl.uniform2f(r_u_mouse, camera_yaw, camera_pitch)
    gl.uniform3fv(r_u_lights, r_light_buffer)

    var vo = 0d
    var last_texture = -1

    for (c <- r_draw_calls) {

      // Bind new texture only if it changed from the previous one. The map
      // is sorted by texture indices, so this helps.
      if (last_texture != c.texture) {
        last_texture = c.texture
        gl.bindTexture(gl.TEXTURE_2D, r_textures(last_texture).gl_texture)
      }

      gl.uniform3f(r_u_pos, c.x, c.y, c.z)
      gl.uniform2f(r_u_rotation, c.yaw, c.pitch)
      gl.uniform1f(r_u_frame_mix, c.mix)

      // If we have two different frames, calculate the offset from the
      // drawArrays call to the mix frame.
      // Setting the vertexAttribPointer is quite expensive, so we only
      // do this if we have to; i.e. for animated models.
      if (vo != (c.offset2-c.offset1)) {
        vo = (c.offset2-c.offset1)
        gl.vertexAttribPointer(r_va_p2, 3, gl.FLOAT, false, 8 * 4, vo*8*4)
        gl.vertexAttribPointer(r_va_n2, 3, gl.FLOAT, false, 8 * 4, (vo*8+5)*4)
      }
      gl.drawArrays(gl.TRIANGLES, c.offset1, c.num_verts)
    }
    // Reset draw calls
    r_draw_calls.clear()
  }

  case class DrawCall(x:Double, y:Double, z:Double, yaw:Double, pitch:Double,
                      texture:Int, offset1:Int, offset2:Int, mix:Int, num_verts:Int)

  def draw(pos: Vec3, yaw: Double, pitch: Double, texture: Int, offset1: Int, offset2: Int, mix: Int, num_verts: Int) = {
    r_draw_calls.addOne(new DrawCall( pos.x, pos.y, pos.z, yaw, pitch, texture, offset1, offset2, mix, num_verts ))
  }

  def submit_buffer() = {
    gl.bufferData(gl.ARRAY_BUFFER, r_buffer.subarray(0, r_num_verts * 8), gl.STATIC_DRAW)
  }

  def start_offset() = {
    r_num_verts
  }

  def push_vert(pos: Vec3, normal: Vec3, u: Double, v: Double) = {
    r_buffer.set(js.Array(pos.x.toFloat, pos.y.toFloat, pos.z.toFloat, u.toFloat, v.toFloat, normal.x.toFloat, normal.y.toFloat, normal.z.toFloat), r_num_verts * 8)
    r_num_verts += 1
  }

  private def r_push_quad(v0: Vec3, v1: Vec3, v2: Vec3, v3: Vec3, u: Double, v: Double) = {
    val n = vec3_face_normal(v0, v1, v2)
    push_vert(v0, n, u, 0)
    push_vert(v1, n, 0, 0)
    push_vert(v2, n, u, v)
    push_vert(v3, n, 0, v)
    push_vert(v2, n, u, v)
    push_vert(v1, n, 0, 0)
  }

  def push_block(x: Double, y: Double, z: Double, sx: Double, sy: Double, sz: Double, texture: Int): Int = {

    println("push_block(): texture = "+texture+",  r_textures.length = "+r_textures.length)

    val canvas = r_textures(texture).canvas

    val index = r_num_verts
    val tx = sx / canvas.width
    val ty = sy / canvas.height
    val tz = sz / canvas.width
    val v0 = vec3(x, y + sy, z)
    val v1 = vec3(x + sx, y + sy, z)
    val v2 = vec3(x, y + sy, z + sz)
    val v3 = vec3(x + sx, y + sy, z + sz)
    val v4 = vec3(x, y, z + sz)
    val v5 = vec3(x + sx, y, z + sz)
    val v6 = vec3(x, y, z)
    val v7 = vec3(x + sx, y, z)

    r_push_quad(v0, v1, v2, v3, tx, tz) // top
    r_push_quad(v4, v5, v6, v7, tx, tz) // bottom
    r_push_quad(v2, v3, v4, v5, tx, ty) // front
    r_push_quad(v1, v0, v7, v6, tx, ty) // back
    r_push_quad(v3, v1, v5, v7, tz, ty) // right
    r_push_quad(v0, v2, v6, v4, tz, ty) // left

    index
  }

  def push_light(pos: Vec3, intensity: Double, r: Double, g: Double, b: Double) = {
    // Calculate the distance to the light, fade it out between 768--1024
    val fade = clamp(scale(vec3_dist(pos, camera), 768, 1024, 1, 0), 0, 1) * intensity * 10
    if (fade > 0 && r_num_lights < R_MAX_LIGHT_V3 / 2) {
      r_light_buffer.set(js.Array(pos.x.toFloat, pos.y.toFloat, pos.z.toFloat, (r * fade).toFloat, (g * fade).toFloat, (b * fade).toFloat), r_num_lights * 6)
      r_num_lights += 1
    }
  }
}
