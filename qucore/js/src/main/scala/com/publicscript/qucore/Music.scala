package com.publicscript.qucore

import com.publicscript.qucore.Audio.{Instrument, Track}

object Music {

  /*
val music_data = [6014,21,88,
                    [
                        [
                             [7,0,0,1,255,0,7,0,0,1,255,0,0,100,0,3636,254,2,1199,254,4,71,0,0,0,0,0,0,0],
                             [1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1],
                             [
                               [126,126,0,0,126,0,0,0,0,0,0,0,0,0,0,0,126,126,0,0,126,0,0,0,0,0,0,0,0,0,0,0]
                             ]
                        ],
                        [
                             [6,0,0,0,255,2,6,0,18,0,255,2,0,100000,56363,100000,199,2,200,254,8,24,0,0,0,0,0,0,0],
                             [0,0,2,2,3,4,2,2,3,5,2,2,3,4,2,2,3,5],
                             [
                                [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                                [132,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                                [133,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,128,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                                [125,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                                [120,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
                             ]
                        ],
                        [
                             [7,0,0,0,87,2,8,0,0,0,16,3,8,0,22,2193,255,3,1162,51,10,182,2,190,0,1,10,96,0],
                             [0,0,0,0,0,0,1,1,1,1,1,1,1,1],
                             [
                                [149,149,0,0,149,0,149,0,149,149,0,0,149,0,149,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
                             ]
                        ],
                        [
                             [8,0,0,0,65,2,6,0,0,0,243,3,0,200,7505,20000,204,4,6180,81,4,198,0,0,0,0,6,131,0],
                             [0,0,0,0,0,0,0,0,0,0,1,1,2,3,1,1,2,3],
                             [
                                [132,0,0,0,0,0,0,0,133,0,0,0,137,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                                [132,0,0,0,0,0,0,0,133,0,0,0,130,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                                [132,0,0,0,0,0,0,0,133,0,0,0,125,0,0,0,0,0,0,0,125,0,0,0,0,0,0,0,0,0,0,0]
                             ]
                        ]
                     ]
                  ]
*/


  private val track1 = new Track(new Instrument(7,0,0,true,255,0,7,0,0,true,255,0,0,100,0,3636,254,2,1199,254,4,71,0,0,false,false,0,0,0),Array(1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1),Array(Array(126,126,0,0,126,0,0,0,0,0,0,0,0,0,0,0,126,126,0,0,126,0,0,0,0,0,0,0,0,0,0,0)))
  private val track2 = new Track(new Instrument(6,0,0,false,255,2,6,0,18,false,255,2,0,100000,56363,100000,199,2,200,254,8,24,0,0,false,false,0,0,0),Array(0,0,2,2,3,4,2,2,3,5,2,2,3,4,2,2,3,5),Array(Array(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(132,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(133,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,128,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(125,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(120,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)))
  private val track3 = new Track(new Instrument(7,0,0,false,87,2,8,0,0,false,16,3,8,0,22,2193,255,3,1162,51,10,182,2,190,false,true,10,96,0),Array(0,0,0,0,0,0,1,1,1,1,1,1,1,1),Array(Array(149,149,0,0,149,0,149,0,149,149,0,0,149,0,149,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)))
  private val track4 = new Track(new Instrument(8,0,0,false,65,2,6,0,0,false,243,3,0,200,7505,20000,204,4,6180,81,4,198,0,0,false,false,6,131,0),Array(0,0,0,0,0,0,0,0,0,0,1,1,2,3,1,1,2,3),Array(Array(132,0,0,0,0,0,0,0,133,0,0,0,137,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(132,0,0,0,0,0,0,0,133,0,0,0,130,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),Array(132,0,0,0,0,0,0,0,133,0,0,0,125,0,0,0,0,0,0,0,125,0,0,0,0,0,0,0,0,0,0,0)))

  val music_data : Array[Track] = Array(track1,track2,track3,track4)

  val row_len=6014
  val pattern_len=21
  val song_len=88

}