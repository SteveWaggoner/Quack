package com.publicscript.qucore

import com.publicscript.qucore.Audio.audio_ctx

import scala.scalajs.js.typedarray.Uint8Array
import com.publicscript.qucore.MathUtils.{Vec3, vec3, vec3_add, vec3_length, vec3_mulf, vec3_normalize, vec3_sub}
import com.publicscript.qucore.Render.{r_draw, r_push_block}
import com.publicscript.qucore.Game.game_spawn
import com.publicscript.qucore.{Entity, EntityPlayer}
import org.scalajs.dom
import org.scalajs.dom.{AudioBuffer, html}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.scalajs.js


object Map {

  var map : MapData = null
  val map_size = 128

  /* Parse map container format
  typedef struct {
    u8 x, y, z;
    u8 sx, sy, sz;
  } block_t;

  typedef struct {
    u8 sentinel;
    u8 tex;
  } block_texture_t;

  typedef struct {
    char type;
    u8 x, y, z;
    u8 data1, data2;
  } entity_t;

  struct {
    u16 blocks_size;
    block_t blocks[];
    u16 num_entities;
    entity_t entities[num_entities];
  } map_data;

  Block data is interleaved with the block_texture_t struct to denote
  the texture index to use for the following blocks.
*/

  case class MapEntity(entity_name:String,x:Int,y:Int,z:Int,data1:Int,data2:Int)
  case class MapRender (b:Int,t:Int)
  case class MapData(cm:Uint8Array,e:Array[MapEntity],r:Array[MapRender])
  // Entity Id to class - must be consistent with map_packer.c line ~900
  val id_to_entity_name = scala.collection.immutable.Map[Int,String](0 -> "player", 1 -> "grunt")

  def parse_map_container(data: Uint8Array): Array[MapData] = {

    val maps = new ArrayBuffer[MapData](0)
    var i = 0
    while (i < data.length) {

      val blocks_size = data(i+0) | (data(i+1) << 8)
      i += 2

      val cm = new Uint8Array(map_size * map_size * map_size >> 3) // collision map

      var r = new ArrayBuffer[MapRender](0)
      var t = -1

      var j = i
      while (j < i + blocks_size) {

        // First value is either the x coordinate or a texture change
        // sentinel value (255) followed by the texture index
        if (data(j+0) == 255) {
          t = data(j+1)
          j += 2
        }

        val x = data(j+0)
        val y = data(j+1)
        val z = data(j+2)
        val sx = data(j+3)
        val sy = data(j+4)
        val sz = data(j+5)

        j += 6

        // Submit the block to the render buffer; we get the vertex offset
        // of this block within the buffer back, so we can draw it later
        val b = r_push_block(x << 5, y << 4, z << 5,
          sx << 5, sy << 4, sz << 5,
          t)


        // The collision map is a bitmap; 8 x blocks per byte
        for (cz <- z until z + sz) {
          for (cy <- y until y + sy) {
            for (cx <- x until x + sx) {
              val cm_ndx = ( (cz * map_size * map_size) + (cy * map_size) + cx ) >> 3
              val bit_val = 1 << (cx & 7)
              cm(cm_ndx) = (cm(cm_ndx) | bit_val).toShort
            }
          }
        }

        r.addOne(MapRender(t=t, b=b))
      }

      i += blocks_size

      // Slice of entity data; we parse it when we actually spawn
      // the entities in map_init()
      val num_entities = data(i + 0) | (data(i + 1) << 8)
      i += 2


      val e = new ArrayBuffer[MapEntity](0)
      var k = i
      while (k < i + num_entities * 6 /*sizeof(entity_t)*/) {

        val ee = new MapEntity(entity_name = id_to_entity_name(data(k+0)), x = data(k+1), y = data(k+2), z = data(k+3), data1 = data(k+4), data2 = data(k+5))
        e.addOne(ee)
        k += 6
      }

      i += num_entities * 6


      maps.addOne(MapData(cm = cm, e.toArray, r.toArray))
    }
    maps.toArray
  }

/*
  import scala.concurrent.Future
  def audio_load_url_async(url:String): Future[AudioBuffer] = {

    import js.Thenable.Implicits._

    val responseAudioBuffer = for {
      response <- dom.fetch(url)
      arrayBuffer <- response.arrayBuffer()
      audioBuffer <- audio_ctx.decodeAudioData(arrayBuffer)
    } yield {
      audioBuffer
    }

    responseAudioBuffer
  }
  */

  def updatePre(pre: html.Pre) = {
    import scala.concurrent
    .ExecutionContext
    .Implicits
    .global
    import js.Thenable.Implicits._
    val url =
      "https://www.boredapi.com/api/activity"
    val responseText = for {
      response <- dom.fetch(url)
      text <- response.text()
    } yield {
      text
    }
    for (text <- responseText)
      pre.textContent = text
  }


  import scala.concurrent.Future
  def map_load_container_async(url: String): Future[Array[MapData]] = {

    import scala.concurrent.ExecutionContext.Implicits.global
    import js.Thenable.Implicits.thenable2future

    val responseMaps = for {
      response <- dom.fetch(url)
      arrayBuffer <- response.arrayBuffer()
    } yield {
      parse_map_container(new Uint8Array(arrayBuffer))
    }

    responseMaps
  }


  def map_load_container (path:String): Array[MapData] = {

    val futureMaps = map_load_container_async(path)

    val maps = Await.ready(futureMaps, Duration.Inf).value.get.get
    return maps
  }

  def map_init(m: MapData) = {
    val map = m

    // Parse entity data and spawn all entities for this map
    for (e <- map.e) {
      spawn_entity(e)
    }
  }

  def spawn_entity(e:MapEntity) : Entity = {
    val pos = vec3(e.x*32,e.y*16,e.z*32)
    game_spawn(e.entity_name, pos, e.data1, e.data2)
  }

  def map_block_beneath(pos:Vec3, size:Vec3):Boolean = {
    map_block_at(pos.x.toInt >> 5, (pos.y - size.y - 8).toInt >> 4, pos.z.toInt >> 5) || map_block_at(pos.x.toInt >> 5, (pos.y - size.y - 24).toInt >> 4, pos.z.toInt >> 5)
  }


  def map_block_at(x: Int, y: Int, z: Int):Boolean = {
    val cell = (z * map_size * map_size + y * map_size + x) >> 3
    val bit = 1 << (x & 7)
    (map.cm(cell) & bit) != 0
  }

  def map_line_of_sight(a_par:Vec3, b:Vec3):Boolean = {
    map_trace(a_par,b)!=null
  }

  def map_trace(a_par: Vec3, b: Vec3):Vec3 = {
    var a = a_par
    val diff = vec3_sub(b, a)
    val step_dir = vec3_mulf(vec3_normalize(diff), 16)
    val steps = vec3_length(diff) / 16
    for (i <- 0 until steps.toInt) {
      a = vec3_add(a, step_dir)
      if (map_block_at(a.x.toInt >> 5, a.y.toInt >> 4, a.z.toInt >> 5)) {
        return a
      }
    }
    null
  }

  def map_block_at_box(box_start: Vec3, box_end: Vec3):Boolean = {
    for (z <- box_start.z.toInt >> 5 to box_end.z.toInt >> 5) {
      for (y <- box_start.y.toInt >> 4 to box_end.y.toInt >> 4) {
        for (x <- box_start.x.toInt >> 5 to box_end.x.toInt >> 5) {
          if (map_block_at(x, y, z)) {
            return true
          }
        }
      }
    }
    false
  }

  def map_draw() = {
    val p = vec3()
    for (r <- map.r) {
      r_draw(p, 0, 0, r.t, r.b,r.b,0,36)
    }
  }

}
