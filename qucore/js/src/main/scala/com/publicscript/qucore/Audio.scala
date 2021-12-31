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
object Audio {

import scala.scalajs.js.Float32Array

val AUDIO_SAMPLERATE = 44100
val AUDIO_TAB_SIZE = 4096
val AUDIO_TAB_MASK = AUDIO_TAB_SIZE - 1
val AUDIO_TAB = new Float32Array(AUDIO_TAB_SIZE * 4)

def audio_init() = {
  val audio_ctx = new AudioContext()
  audio_ctx.resume()
  // Generate the lookup tables
  for (i <- 0 until AUDIO_TAB_SIZE) {
    AUDIO_TAB(i) = Math.sin(i * 6.283184 / AUDIO_TAB_SIZE) // sin
    AUDIO_TAB(i + AUDIO_TAB_SIZE) = if (AUDIO_TAB(i) < 0) -1 else 1 // square
    AUDIO_TAB(i + AUDIO_TAB_SIZE * 2) = i / AUDIO_TAB_SIZE - 0.5 // saw
    AUDIO_TAB(i + AUDIO_TAB_SIZE * 3) = if (i < AUDIO_TAB_SIZE / 2) i / (AUDIO_TAB_SIZE / 4) - 1 else 3 - i / (AUDIO_TAB_SIZE / 4) // tri
  }
}

def audio_play(buffer: Any, volume: Double = 1, loop: Double = 0, pan: Double = 0) = {
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

def audio_get_ctx_buffer(buf_l: Array[Double], buf_r: Array[Double]) = {
  val buffer = audio_ctx.createBuffer(2, buf_l.length, AUDIO_SAMPLERATE)
  buffer.getChannelData(0).set(buf_l)
  buffer.getChannelData(1).set(buf_r)
  buffer
}

def audio_generate_sound(row_len: Double, note: Any, buf_l: Array[Double], buf_r: Array[Double], write_pos: Double, 
// Instrument properties
osc1_oct: Double, osc1_det: Any, osc1_detune: Double, osc1_xenv: Any, osc1_vol: Double, osc1_waveform: Double, osc2_oct: Double, osc2_det: Any, osc2_detune: Double, osc2_xenv: Any, osc2_vol: Double, osc2_waveform: Double, noise_fader: Double, attack: Double, sustain: Double, release: Double, master: Double, fx_filter: Any, fx_freq: Double, fx_resonance: Double, fx_delay_time: Any, fx_delay_amt: Any, fx_pan_freq_p: Double, fx_pan_amt: Double, lfo_osc1_freq: Any, lfo_fx_freq: Any, lfo_freq_p: Double, lfo_amt: Double, lfo_waveform: Double) = {
  val osc_lfo_offset = lfo_waveform * AUDIO_TAB_SIZE
  val osc1_offset = osc1_waveform * AUDIO_TAB_SIZE
  val osc2_offset = osc2_waveform * AUDIO_TAB_SIZE
  val fx_pan_freq = Math.pow(2, fx_pan_freq_p - 8) / row_len
  val lfo_freq = Math.pow(2, lfo_freq_p - 8) / row_len
  var c1 = 0
  var c2 = 0
  val q = fx_resonance / 255
  var low = 0
  var band = 0
  var high = 0
  val buf_length = buf_l.length
  val num_samples = attack + sustain + release - 1
  val osc1_freq = Math.pow(1.059463094, note + (osc1_oct - 8) * 12 + osc1_det - 128) * 0.00390625 * (1 + 0.0008 * osc1_detune)
  val osc2_freq = Math.pow(1.059463094, note + (osc2_oct - 8) * 12 + osc2_det - 128) * 0.00390625 * (1 + 0.0008 * osc2_detune)
  for (j <- num_samples to 0 by -1) {
    val k = j + write_pos
    val lfor = AUDIO_TAB(osc_lfo_offset + ((k * lfo_freq * AUDIO_TAB_SIZE) & AUDIO_TAB_MASK)) * lfo_amt / 512 + 0.5
    var sample = 0
    var filter_f = fx_freq
    var envelope = 1
    // Envelope
    if (j < attack) {
      envelope = j / attack
    } else if (j >= attack + sustain) {
      envelope -= (j - attack - sustain) / release
    }
    // Oscillator 1
    var temp_f = osc1_freq
    if (lfo_osc1_freq) {
      temp_f *= lfor
    }
    if (osc1_xenv) {
      temp_f *= envelope * envelope
    }
    c1 += temp_f
    sample += AUDIO_TAB(osc1_offset + ((c1 * AUDIO_TAB_SIZE) & AUDIO_TAB_MASK)) * osc1_vol
    // Oscillator 2
    temp_f = osc2_freq
    if (osc2_xenv) {
      temp_f *= envelope * envelope
    }
    c2 += temp_f
    sample += AUDIO_TAB(osc2_offset + ((c2 * AUDIO_TAB_SIZE) & AUDIO_TAB_MASK)) * osc2_vol
    // Noise oscillator
    if (noise_fader) {
      sample += (2 * Math.random() - 1) * noise_fader * envelope
    }
    sample *= envelope / 255
    // State variable filter
    if (lfo_fx_freq) {
      filter_f *= lfor
    }
    filter_f = 1.5 * AUDIO_TAB((filter_f * 0.5 / AUDIO_SAMPLERATE * AUDIO_TAB_SIZE) & AUDIO_TAB_MASK)
    low += filter_f * band
    high = q * (sample - band) - low
    band += filter_f * high
    sample = Array(sample, high, low, band, low + high)(fx_filter)
    // Panning & master volume
    temp_f = AUDIO_TAB((k * fx_pan_freq * AUDIO_TAB_SIZE) & AUDIO_TAB_MASK) * fx_pan_amt / 512 + 0.5
    sample *= 0.00476 * master // 39 / 8192 = 0.00476
    buf_l(k) += sample * (1 - temp_f)
    buf_r(k) += sample * temp_f
  }
}

def audio_create_song(row_len: Double, pattern_len: Double, song_len: Double, tracks: Any) = {
  val num_samples = AUDIO_SAMPLERATE * song_len
  val mix_buf_l = new Float32Array(num_samples)
  val mix_buf_r = new Float32Array(num_samples)
  for (track <- tracks) {
  		val buf_l = new Float32Array(num_samples),
  			buf_r = new Float32Array(num_samples),
  			write_pos = 0,
  			delay_shift = (track[0/*instrument*/][20/*fx_delay_time*/] * row_len) >> 1,
  			delay_amount = track[0/*instrument*/][21/*fx_delay_amt*/] / 255;
  
  		for (let p = 0; p < pattern_len; p++) {
  			for (let row = 0; row < 32; row++) {
  				//let note = track[2/*notes*/][track[1/*pattern*/][p] - 1]?.[row];
  				let note = track[2/*notes*/][track[1/*pattern*/][p] - 1][row];
  				if (note) {
  					audio_generate_sound(row_len, note, buf_l, buf_r, write_pos, ...track[0/*instrument*/]);
  				}
  				write_pos += row_len;
  			}
  		}
  
  		audio_apply_delay(delay_shift, delay_amount, buf_l, buf_r);
  		for (let b = 0; b < num_samples; b++) {
  			mix_buf_l[b] += buf_l[b];
  			mix_buf_r[b] += buf_r[b];
  		}
  	}
  audio_get_ctx_buffer(mix_buf_l, mix_buf_r)
}

def audio_create_sound(note: Any, instrument: Array[Double], row_len: Double = 5605) = {
  val delay_shift = (instrument(20) * row_len) >> 1
  val delay_amount = instrument(21) /  /*fx_delay_amt*/255
  val num_samples = instrument(13) +  /*env_attack*/instrument(14) +  /*env_sustain*/instrument(15) +  /*env_release*/delay_shift * 32 * delay_amount
  val buf_l = new Float32Array(num_samples)
  val buf_r = new Float32Array(num_samples)
  audio_generate_sound(row_len, note, buf_l, buf_r, 0, /* Unsupported: SpreadElement */ ...instrument
  )
  audio_apply_delay(delay_shift, delay_amount, buf_l, buf_r)
  audio_get_ctx_buffer(buf_l, buf_r)
}

def audio_apply_delay(shift: Double, amount: Double, buf_l: Array[Double], buf_r: Array[Double]) = {
  for (i <- 0 until buf_l.length - shift) {
    buf_l(i + shift) += buf_r(i) * amount
    buf_r(i + shift) += buf_l(i) * amount
  }
}
}
