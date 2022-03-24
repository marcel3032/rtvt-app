import json
import math

from PIL import Image

colorsMap = {
    "black": "#000000",
    "red": "#ff0000",
    "green": "#00ff00",
    "blue": "#0000ff",
    "yellow": "#f6ff00",
    "cyan": "#00ddff",
    "magenta": "#ff00a2",
    "orange": "#ff7b00",
    "white": "#ffffff",
    "brown": "#853100",
    "light green": "#a9ff91",
    "dark green": "#0e4200",
    "azure": "#628bc4",
    "purple": "#8d0aff"}


def hex2rgb(hex):
    return tuple(int(hex[i:i + 2], 16) for i in (1, 3, 5))


def distance(rgb1, rgb2):
    return math.sqrt((rgb1[0] - rgb2[0]) ** 2 + (rgb1[1] - rgb2[1]) ** 2 + (rgb1[2] - rgb2[2]) ** 2)


def find_nearest_color(r, g, b):
    best = "red"
    for color in colorsMap:
        if distance((r, g, b), hex2rgb(colorsMap[best])) > distance((r, g, b), hex2rgb(colorsMap[color])):
            best = color
    return best


def make_image(name):
    im = Image.open(name)
    pixels = im.load()

    res_image = [list() for _ in range(im.size[1])]

    for i in range(im.size[0]):
        for j in range(im.size[1]):
            rgb = pixels[i, j]
            res_image[j].append({"need_color": find_nearest_color(*rgb[:3]),
                                 "display_color": '#{:02x}{:02x}{:02x}'.format(*rgb[:3]),
                                 "precolored": True})

    return res_image


res_images = [make_image("bear.png"),
              make_image("bulb.png"),
              make_image("car.png"),
              make_image("carrot.png"),
              make_image("dog.png"),
              make_image("duck.png"),
              make_image("elephant.png"),
              make_image("fish.png"),
              make_image("flower.png"),
              make_image("goat.png"),
              make_image("heart.png"),
              make_image("house.png"),
              make_image("mouse.png"),
              make_image("phone.png"),
              make_image("piggy.png"),
              make_image("rabbit.png"),
              make_image("smile.png"),
              make_image("tree.png"),
              make_image("ua.png"),
              make_image("watermelon.png")]

with open('pictures.json', 'w') as fout:
    print(json.dumps(res_images, indent=2), file=fout)
