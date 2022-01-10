package com.publicscript.qucore

import scala.scalajs.js.typedarray.Uint8Array

import com.publicscript.qucore.MathUtils.{Vec3, vec3,vec3_sub,vec3_add,vec3_mulf,vec3_normalize,vec3_length}


object Map {
  val map_size = 128
  val map_load_container = /* Unsupported: AsyncArrowFunctionExpression */ async (path) => {
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

    let data = new Uint8Array(await (await fetch(path)).arrayBuffer()),
    maps = [];
    for (let i = 0; i < data.length;) {
      let blocks_size = data[i++] | (data[i++] << 8),
      cm = new Uint8Array(map_size * map_size * map_size >> 3), // collision map
      b = data.subarray(i, i += blocks_size),
      r = [],
      t;

      // Parse block data and construct geometry and the collision map
      for (let j = 0; j < b.length;) {

        // First value is either the x coordinate or a texture change
        // sentinel value (255) followed by the texture index
        if (b[j] == 255) {
          j++;
          t = b[j++];
        }
        let
        x = b[j++],  y = b[j++],  z = b[j++],
        sx = b[j++], sy = b[j++], sz = b[j++];

        // Submit the block to the render buffer; we get the vertex offset
        // of this block within the buffer back, so we can draw it later
        r.push({
          t,
          b: r_push_block(
            x << 5, y << 4, z << 5,
            sx << 5, sy << 4, sz << 5,
            t
          )
        });

        // The collision map is a bitmap; 8 x blocks per byte
        for (let cz = z; cz < z + sz; cz++) {
          for (let cy = y; cy < y + sy; cy++) {
            for (let cx = x; cx < x + sx; cx++) {
              cm[
                (
                  cz * map_size * map_size +
                  cy * map_size +
                  cx
              ) >> 3
              ] |= 1 << (cx & 7);
            }
          }
        }
      }

      // Slice of entity data; we parse it when we actually spawn
      // the entities in map_init()
      let num_entities = data[i++] | (data[i++] << 8),
      e = data.subarray(i, i += num_entities * 6 /*sizeof(entity_t)*/);
      maps.push({cm, e, r});
    }
    return maps;
  }

  def map_init(m: Any) = {
    val map = m
    // Entity Id to class - must be consistent with map_packer.c line ~900
    val spawn_class = Array(
      /*00 */entity_player_t,
      /*01 */entity_enemy_grunt_t,
      /*02 */entity_enemy_enforcer_t,
      /*03 */entity_enemy_ogre_t,
      /*04 */entity_enemy_zombie_t,
      /*05 */entity_enemy_hound_t,
      /*06 */entity_pickup_nailgun_t,
      /*07 */entity_pickup_grenadelauncher_t,
      /*08 */entity_pickup_health_t,
      /*09 */entity_pickup_nails_t,
      /*10 */entity_pickup_grenades_t,
      /*11 */entity_barrel_t,
      /*12 */entity_light_t,
      /*13 */entity_trigger_level_t,
      /*14 */entity_door_t,
      /*15 */entity_pickup_key_t,
      /*16 */entity_torch_t
    )
      // Parse entity data and spawn all entities for this map
    {
      var i = 0
      while (i < map.e.length) {
        {
          val `type` = spawn_class(m.e({
            val temp = i
            i += 1
            temp
          }))
          game_spawn(`type`, vec3(m.e({
            val temp = i
            i += 1
            temp
          }) * 32, m.e({
            val temp = i
            i += 1
            temp
          }) * 16, m.e({
            val temp = i
            i += 1
            temp
          }) * 32), m.e({
            val temp = i
            i += 1
            temp
          }), m.e({
            val temp = i
            i += 1
            temp
          }))
        }
      }
    }
  }

  def map_block_at(x: Double, y: Double, z: Double):Boolean = {
    map.cm((z * map_size * map_size + y * map_size + x).toInt >> 3) & (1 << (x.toInt & 7))
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
    /* Unsupported: ForOfStatement */ for (let r of map.r) {
      r_draw(p, 0, 0, r.t, r.b,r.b,0,36);
    }
  }

}
