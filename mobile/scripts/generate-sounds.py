#!/usr/bin/env python3
"""Synthesizes CalmLoop's UI sounds into assets/sounds/ (stdlib only, deterministic).

Character: soft, warm, low-volume tones (singing-bowl partials, slow attacks) so nothing
startles someone who is already anxious. Re-run after editing: `npm run sounds`.
"""
import math
import os
import struct
import wave

RATE = 22050
OUT = os.path.join(os.path.dirname(__file__), "..", "assets", "sounds")


def bell(freq, dur, amp=0.25, decay=3.0, attack=0.01, partials=((1, 1.0), (2.76, 0.18), (5.4, 0.06))):
    n = int(RATE * dur)
    out = []
    for i in range(n):
        t = i / RATE
        env = min(1.0, t / attack) * math.exp(-decay * t)
        s = sum(a * math.sin(2 * math.pi * freq * m * t) for m, a in partials)
        out.append(amp * env * s)
    return out


def glide(f0, f1, dur, amp=0.18, attack=0.35, release=0.6):
    """Smooth pitch glide with long fades: a breath-like swell."""
    n = int(RATE * dur)
    out, phase = [], 0.0
    for i in range(n):
        t = i / RATE
        f = f0 + (f1 - f0) * (0.5 - 0.5 * math.cos(math.pi * t / dur))
        phase += 2 * math.pi * f / RATE
        env = min(1.0, t / attack) * min(1.0, (dur - t) / release)
        out.append(amp * env * (math.sin(phase) + 0.25 * math.sin(2 * phase)))
    return out


def mix(*parts):
    """parts: (offset_seconds, samples)"""
    length = max(int(off * RATE) + len(s) for off, s in parts)
    buf = [0.0] * length
    for off, s in parts:
        start = int(off * RATE)
        for i, v in enumerate(s):
            buf[start + i] += v
    return buf


def write(name, samples):
    peak = max(1e-9, max(abs(v) for v in samples))
    scale = min(1.0, 0.9 / peak)
    with wave.open(os.path.join(OUT, name), "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(RATE)
        w.writeframes(b"".join(struct.pack("<h", int(v * scale * 32767)) for v in samples))


def quiet(samples, gain):
    return [v * gain for v in samples]


os.makedirs(OUT, exist_ok=True)
# Every tap: a tiny muted "tock".
write("tap.wav", quiet(bell(880, 0.08, decay=45, attack=0.002, partials=((1, 1.0), (2.0, 0.2))), 0.35))
# Toggle / selection.
write("select.wav", quiet(bell(1175, 0.12, decay=30, attack=0.002), 0.35))
# Breathing cues.
write("inhale.wav", glide(220, 330, 3.6))
write("hold.wav", quiet(bell(392, 1.6, decay=2.2, attack=0.03), 0.6))
write("exhale.wav", glide(330, 196, 3.6))
# Grounding step complete: two-note rising chime.
write("step.wav", mix((0, bell(523.25, 1.0, decay=4)), (0.12, bell(784.0, 1.2, decay=3.5))))
# Session complete / entry saved: gentle major arpeggio.
write("success.wav", mix(
    (0, bell(392.0, 1.6, decay=2.5)),
    (0.14, bell(493.88, 1.6, decay=2.5)),
    (0.28, bell(587.33, 1.8, decay=2.2)),
    (0.42, bell(783.99, 2.0, decay=2.0, amp=0.18)),
))
print("Wrote sounds to", os.path.abspath(OUT))
