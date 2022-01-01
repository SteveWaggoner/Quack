package com.publicscript.qucore

/* ondrejspanel.github.io/ScalaFromJS/: Web*/

// Gutted for js13k and modified to use Float32 buffers directly
// ~ Dominic Szablewski, phoboslab.org, Sep 2018
// Almost re-written for for jsk13 2019. Oscilators now use a lookup table
// instead of calling functions. This and various other changes result in a
// ~10x performance increase and smaller file size.
// ~ Dominic Szablewski, phoboslab.org, Sep 2019
// Again updated for js13k 2021. Song and sound definitions are now just arrays
// instead of objects.
//
// Sonant-X
//
// Copyr (c) 2014 Nicolas Vanhoren
//
// Sonant-X is a fork of js-sonant by Marcus Geelnard and Jake Taylor. It is
// still published using the same license (zlib license, see below).
//
// Copyr (c) 2011 Marcus Geelnard
// Copyr (c) 2008-2009 Jake Taylor
//
// This software is provided 'as-is', without any express or implied
// warranty. In no event will the authors be held liable for any damages
// arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it
// freely, subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented; you must not
//	claim that you wrote the original software. If you use this software
//	in a product, an acknowledgment in the product documentation would be
//	appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such, and must not be
//	misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source
//	distribution.

//import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

object Audio {

  import scala.scalajs.js.typedarray.Float32Array
  import scala.scalajs.js.Math

  import scala.scalajs.js

  import org.scalajs.dom.AudioContext
  import org.scalajs.dom.AudioBuffer
  import org.scalajs.dom


  private var audio_ctx: AudioContext = null

  val AUDIO_SAMPLERATE = 44100
  val AUDIO_TAB_SIZE = 4096
  val AUDIO_TAB_MASK = AUDIO_TAB_SIZE - 1
  val AUDIO_TAB = new Float32Array(AUDIO_TAB_SIZE * 4)

  def audio_init() = {
    audio_ctx = new AudioContext()
    audio_ctx.resume()
    // Generate the lookup tables
    for (i <- 0 until AUDIO_TAB_SIZE) {
      AUDIO_TAB(i) = Math.sin(i * 6.283184f / AUDIO_TAB_SIZE).toFloat // sin
      AUDIO_TAB(i + AUDIO_TAB_SIZE) = if (AUDIO_TAB(i) < 0) -1 else 1 // square
      AUDIO_TAB(i + AUDIO_TAB_SIZE * 2) = i / AUDIO_TAB_SIZE - 0.5f // saw
      AUDIO_TAB(i + AUDIO_TAB_SIZE * 3) = if (i < AUDIO_TAB_SIZE / 2) i / (AUDIO_TAB_SIZE / 4) - 1 else 3 - i / (AUDIO_TAB_SIZE / 4) // tri
    }
  }

  def audio_play(buffer: AudioBuffer, volume: Double = 1, loop: Boolean = false, pan: Double = 0) = {
    val gain = audio_ctx.createGain()
    val source = audio_ctx.createBufferSource()
    val panner = audio_ctx.createStereoPanner()
    gain.gain.value = volume
    gain.connect(audio_ctx.destination)
    panner.connect(gain)
    panner.pan.value = pan
    source.buffer = buffer
    source.loop = loop
    source.connect(panner)
    source.start()
  }
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

    responseAudioBuffer onComplete {
      result => audio_play(result.get, 1, false, 0)
    }

    responseAudioBuffer
  }

  def audio_play_async(buffer: Future[AudioBuffer], volume: Double = 1, loop: Boolean = false, pan: Double = 0) = {

    buffer onComplete {
      result => audio_play(result.get, volume, loop, pan)
    }
  }


    /*

  (function () {
    'use strict';

    const URL = 'https://s3-us-west-2.amazonaws.com/s.cdpn.io/123941/Yodel_Sound_Effect.mp3';

    const context = new AudioContext();
    const playButton = document.querySelector('#play');

    let yodelBuffer;

    window.fetch(URL)
      .then(response => response.arrayBuffer())
      .then(arrayBuffer => context.decodeAudioData(arrayBuffer))
      .then(audioBuffer => {
        playButton.disabled = false;
        yodelBuffer = audioBuffer;
      });

    playButton.onclick = () => play(yodelBuffer);

    function play(audioBuffer) {
      const source = context.createBufferSource();
      source.buffer = audioBuffer;
      source.connect(context.destination);
      source.start();
    }
  }());

*/
  def audio_get_ctx_buffer(buf_l: Float32Array, buf_r: Float32Array) = {
    val buffer = audio_ctx.createBuffer(2, buf_l.length, AUDIO_SAMPLERATE)
    buffer.getChannelData(0).set(buf_l)
    buffer.getChannelData(1).set(buf_r)
    buffer
  }

  case class Instrument(osc1_oct: Double, osc1_det: Double, osc1_detune: Double, osc1_xenv: Boolean, osc1_vol: Double, osc1_waveform: Int, osc2_oct: Double, osc2_det: Double, osc2_detune: Double, osc2_xenv: Boolean, osc2_vol: Double, osc2_waveform: Int, noise_fader: Double, attack: Int, sustain: Int, release: Int, master: Double, fx_filter: Int, fx_freq: Double, fx_resonance: Double, fx_delay_time: Int, fx_delay_amt: Int, fx_pan_freq_p: Double, fx_pan_amt: Double, lfo_osc1_freq: Boolean, lfo_fx_freq: Boolean, lfo_freq_p: Double, lfo_amt: Double, lfo_waveform: Long)


  def audio_generate_sound(row_len: Int, note: Float, buf_l: Float32Array, buf_r: Float32Array, write_pos: Int, instrument: Instrument) = {
    val osc_lfo_offset = instrument.lfo_waveform * AUDIO_TAB_SIZE
    val osc1_offset = instrument.osc1_waveform * AUDIO_TAB_SIZE
    val osc2_offset = instrument.osc2_waveform * AUDIO_TAB_SIZE
    val fx_pan_freq = Math.pow(2, instrument.fx_pan_freq_p - 8) / row_len
    val lfo_freq = (Math.pow(2, instrument.lfo_freq_p - 8) / row_len).toInt
    var c1 = 0d
    var c2 = 0d
    val q = instrument.fx_resonance / 255
    var low = 0d
    var band = 0d
    var high = 0d
    val buf_length = buf_l.length
    val num_samples = instrument.attack + instrument.sustain + instrument.release - 1
    val osc1_freq = Math.pow(1.059463094, note + (instrument.osc1_oct - 8) * 12 + instrument.osc1_det - 128) * 0.00390625 * (1 + 0.0008 * instrument.osc1_detune)
    val osc2_freq = Math.pow(1.059463094, note + (instrument.osc2_oct - 8) * 12 + instrument.osc2_det - 128) * 0.00390625 * (1 + 0.0008 * instrument.osc2_detune)
    for (j <- num_samples to 0 by -1) {
      val k = j + write_pos
      val lfor = AUDIO_TAB((osc_lfo_offset + ((k * lfo_freq * AUDIO_TAB_SIZE) & AUDIO_TAB_MASK)).toInt) * instrument.lfo_amt / 512 + 0.5
      var sample = 0d
      var filter_f = instrument.fx_freq
      var envelope = 1
      // Envelope
      if (j < instrument.attack) {
        envelope = j / instrument.attack
      } else if (j >= instrument.attack + instrument.sustain) {
        envelope -= (j - instrument.attack - instrument.sustain) / instrument.release
      }
      // Oscillator 1
      var temp_f = osc1_freq
      if (instrument.lfo_osc1_freq) {
        temp_f *= lfor
      }
      if (instrument.osc1_xenv) {
        temp_f *= envelope * envelope
      }
      c1 += temp_f
      sample += AUDIO_TAB(osc1_offset + ((c1 * AUDIO_TAB_SIZE).toInt & AUDIO_TAB_MASK)) * instrument.osc1_vol
      // Oscillator 2
      temp_f = osc2_freq
      if (instrument.osc2_xenv) {
        temp_f *= envelope * envelope
      }
      c2 += temp_f
      sample += AUDIO_TAB(osc2_offset + ((c2 * AUDIO_TAB_SIZE).toInt & AUDIO_TAB_MASK)) * instrument.osc2_vol
      // Noise oscillator
      if (instrument.noise_fader != 0) {
        sample += (2 * Math.random() - 1) * instrument.noise_fader * envelope
      }
      sample *= envelope / 255
      // State variable filter
      if (instrument.lfo_fx_freq) {
        filter_f *= lfor
      }
      filter_f = 1.5 * AUDIO_TAB((filter_f * 0.5 / AUDIO_SAMPLERATE * AUDIO_TAB_SIZE).toInt & AUDIO_TAB_MASK)
      low += filter_f * band
      high = q * (sample - band) - low
      band += filter_f * high
      sample = Array(sample, high, low, band, low + high)(instrument.fx_filter)
      // Panning & master volume
      temp_f = AUDIO_TAB((k * fx_pan_freq * AUDIO_TAB_SIZE).toInt & AUDIO_TAB_MASK) * instrument.fx_pan_amt / 512 + 0.5
      sample *= 0.00476 * instrument.master // 39 / 8192 = 0.00476
      buf_l(k) += (sample * (1 - temp_f)).toFloat
      buf_r(k) += (sample * temp_f).toFloat
    }
  }

  case class Track(instrument: Instrument, pattern: Array[Int], notes: Array[Array[Float]])

  def audio_create_song(row_len: Int, pattern_len: Int, song_len: Int, tracks: Array[Track]) = {
    val num_samples = AUDIO_SAMPLERATE * song_len
    val mix_buf_l = new Float32Array(num_samples)
    val mix_buf_r = new Float32Array(num_samples)
    for (track <- tracks) {
      val buf_l = new Float32Array(num_samples)
      val buf_r = new Float32Array(num_samples)
      var write_pos = 0

      val delay_shift = (track.instrument.fx_delay_time * row_len) >> 1
      val delay_amount = track.instrument.fx_delay_amt / 255

      for (p <- 0 to pattern_len) {
        for (row <- 0 to 32) {

          val note = track.notes(track.pattern(p) - 1)(row)
       //   if (note) {
            audio_generate_sound(row_len, note, buf_l, buf_r, write_pos, track.instrument)
       //   }
          write_pos += row_len
        }
      }

      audio_apply_delay(delay_shift, delay_amount, buf_l, buf_r)
      for (b <- 0 to num_samples) {
        mix_buf_l(b) += buf_l(b)
        mix_buf_r(b) += buf_r(b)
      }
    }
    audio_get_ctx_buffer(mix_buf_l, mix_buf_r)
  }

  def audio_create_sound(note: Float, instrument: Instrument, row_len: Int = 5605) = {
    val delay_shift = (instrument.fx_delay_time * row_len).toInt >> 1
    val delay_amount = instrument.fx_delay_amt / 255
    val num_samples = (instrument.attack + instrument.sustain + instrument.release + delay_shift * 32 * delay_amount).toInt
    val buf_l = new Float32Array(num_samples)
    val buf_r = new Float32Array(num_samples)
    audio_generate_sound(row_len, note, buf_l, buf_r, 0, instrument)

    audio_apply_delay(delay_shift, delay_amount, buf_l, buf_r)
    audio_get_ctx_buffer(buf_l, buf_r)
  }

  def audio_apply_delay(shift: Int, amount: Float, buf_l: Float32Array, buf_r: Float32Array) = {
    for (i <- 0 until buf_l.length - shift) {
      buf_l(i + shift) += buf_r(i) * amount
      buf_r(i + shift) += buf_l(i) * amount
    }
  }
}
