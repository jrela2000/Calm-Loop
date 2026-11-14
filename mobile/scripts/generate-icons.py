#!/usr/bin/env python3
"""Builds app icons from the CalmLoop logo (assets/brand/logo-source.jpg).

Outputs: icon.png (iOS/web, 1024), android adaptive foreground/background/monochrome,
splash-icon.png and favicon.png in assets/images/.
"""
import os
from PIL import Image

ROOT = os.path.join(os.path.dirname(__file__), "..", "assets")
SRC = os.path.join(ROOT, "brand", "logo-source.jpg")
OUT = os.path.join(ROOT, "images")

src = Image.open(SRC).convert("RGB")
bg = src.getpixel((40, 40))

# Alpha from distance to the cream background, so the line art can sit on any color.
def cutout(img):
    rgba = img.convert("RGBA")
    px = rgba.load()
    w, h = rgba.size
    for y in range(h):
        for x in range(w):
            r, g, b, _ = px[x, y]
            d = abs(r - bg[0]) + abs(g - bg[1]) + abs(b - bg[2])
            a = max(0, min(255, int((d - 18) * 4)))
            px[x, y] = (r, g, b, a)
    return rgba

logo = cutout(src).crop((200, 200, 824, 824))  # the circle mark, edge to edge

def on_canvas(mark, size, fraction, fill=(0, 0, 0, 0)):
    canvas = Image.new("RGBA", (size, size), fill)
    side = int(size * fraction)
    m = mark.resize((side, side), Image.LANCZOS)
    canvas.alpha_composite(m, ((size - side) // 2, (size - side) // 2))
    return canvas

os.makedirs(OUT, exist_ok=True)
cream = (*bg, 255)
on_canvas(logo, 1024, 0.72, cream).convert("RGB").save(os.path.join(OUT, "icon.png"))
on_canvas(logo, 1024, 0.56).save(os.path.join(OUT, "android-icon-foreground.png"))  # inside the 66% safe zone
Image.new("RGB", (1024, 1024), bg).save(os.path.join(OUT, "android-icon-background.png"))
mono = on_canvas(logo, 1024, 0.56)
white = Image.new("RGBA", mono.size, (255, 255, 255, 255))
white.putalpha(mono.getchannel("A"))
white.save(os.path.join(OUT, "android-icon-monochrome.png"))
on_canvas(logo, 512, 1.0).save(os.path.join(OUT, "splash-icon.png"))
on_canvas(logo, 48, 0.9, cream).save(os.path.join(OUT, "favicon.png"))
print("background color: #%02X%02X%02X" % bg)
