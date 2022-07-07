package com.publicscript.qucore

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.typedarray.{Float32Array, Uint8Array}
import org.scalajs.dom
import com.publicscript.qucore.MathUtils.{Vec3, vec3, vec3_face_normal}

object Model {

  case class RmfVert(x: Int, y: Int, z: Int)
  case class RmfIndices(a_address_inc: Int, b_index: Int, c_index: Int)
  case class RmfModel(num_frames: Int, num_vertices: Int, num_indices: Int, vertices: Array[RmfVert] = new Array[RmfVert](0), indices: Array[RmfIndices] = new Array[RmfIndices](0))

  /* Parse Retarded Model Format (.rmf):
    struct {
      u8 num_frames;
      u8 num_verts; // per frame
      u8 num_indices;
      struct {
        u8 x, y, z;
      } verts[num_frames * num_verts];
      struct {
        u8 a_address_inc, b_index, c_index;
      } indices[num_indices];
    } rmf_data;
  */


  def parse_model_container(data: Uint8Array): Array[RmfModel] = {

    val models = new ArrayBuffer[RmfModel](0)
    var i = 0
    while (i < data.length) {

      // let model_size = num_frames * num_verts * 3 + num_indices * 3

      val num_frames = data(i + 0)
      val num_verts = data(i + 1)
      val num_indices = data(i + 2)
      i += 3

      val verts = new ArrayBuffer[RmfVert](0)
      for (j <- 0 until num_frames * num_verts) {
        val vert = new RmfVert(data(i + 0), data(i + 1), data(i + 2))
        verts.addOne(vert)
        i += 3
      }

      val indices = new ArrayBuffer[RmfIndices](0)
      for (j <- 0 until num_indices) {
        val indice = new RmfIndices(data(i + 0), data(i + 1), data(i + 2))
        indices.addOne(indice)
        i += 3
      }

      models.addOne(new RmfModel(num_frames, num_verts, num_indices, verts.toArray, indices.toArray))
    }
    models.toArray
  }


  import scala.concurrent.Future

  def model_load_container_async(url: String): Future[Array[Model.RmfModel]] = {

    import scala.concurrent.ExecutionContext.Implicits.global
    import js.Thenable.Implicits.thenable2future
    import js.Thenable.Implicits._

    val responseModels = for {
      response <- dom.fetch(url)
      arrayBuffer <- response.arrayBuffer()
    } yield {
      Model.parse_model_container(new Uint8Array(arrayBuffer))
    }

    responseModels
  }



  case class UV(u: Double, v: Double)
  case class ModelRender(frames: Array[Int], var num_verts: Int)
}


class Model(render: Render) {

  def model_init(model: Model.RmfModel, sx: Double = 1, sy: Double = 1, sz: Double = 1) : Model.ModelRender = {
    // Load header, prepare buffers
    var j = 0

    val vertices = new Float32Array(model.num_vertices * model.num_frames * 3)
    val indices = new Uint8Array(model.num_indices * 3)

    var index_increment = 0
    val offset = 2

    var min_x = 16f
    var max_x = -16f
    var min_y = 16f
    var max_y = -16f

    var i = 0

    for (j <- 0 until model.vertices.length) {

      vertices(i + 0) = (model.vertices(j).x.toFloat - 15) * sx.toFloat
      vertices(i + 1) = (model.vertices(j).y.toFloat - 15) * sy.toFloat
      vertices(i + 2) = (model.vertices(j).z.toFloat - 15) * sz.toFloat

      // Find min/max only for the first frame
      if (i < model.num_vertices * 3) {
        min_x = Math.min(min_x, vertices(i))
        max_x = Math.max(max_x, vertices(i))
        min_y = Math.min(min_y, vertices(i + 1))
        max_y = Math.max(max_y, vertices(i + 1))
      }

      i += 3
    }

    // Load indices, 1x 2bit increment, 2x 7bit absolute
    val frames = new ArrayBuffer[Int](0)


    i=0
    for (j <- 0 until model.num_indices) {

      index_increment += model.indices(j).a_address_inc

      indices(i + 0) = index_increment.toShort
      indices(i + 1) = model.indices(j).b_index.toShort
      indices(i + 2) = model.indices(j).c_index.toShort

      i += 3
    }

    // UV coords in texture space and width/height as fraction of model size
    val uf = 1 / (max_x - min_x)
    val u = -min_x * uf
    val vf = -1 / (max_y - min_y)
    val v = max_y * vf

    // Compute normals for each frame and face and submit to render buffer.
    // Capture the current vertex offset for the first vertex of each frame.
    for (frame_index <- 0 until model.num_frames) {

      frames.addOne(render.start_offset())

      val vertex_offset = frame_index * model.num_vertices * 3

      val mv = new Array[Vec3](3)
      val uv = new Array[Model.UV](3)

      for (i <- 0 until model.num_indices * 3 by 3) {

        for (face_vertex <- 0 to 2) {
          val idx = indices(i + face_vertex) * 3

          mv(face_vertex) = vec3(vertices(vertex_offset + idx + 0), vertices(vertex_offset + idx + 1), vertices(vertex_offset + idx + 2))
          uv(face_vertex) = Model.UV(u = vertices(idx + 0) * uf + u, v = vertices(idx + 1) * vf + v)
        }

        val n = vec3_face_normal(mv(2), mv(1), mv(0))

        render.push_vert(mv(2), n, uv(2).u, uv(2).v)
        render.push_vert(mv(1), n, uv(1).u, uv(1).v)
        render.push_vert(mv(0), n, uv(0).u, uv(0).v)
      }
    }

    new Model.ModelRender(
      frames = frames.toArray,
      num_verts = model.num_indices * 3
    )
  }
}


