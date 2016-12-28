#!/usr/bin/python

# convert sideways tee into 6 images.


#       pamcut [-left leftcol] [-right rightcol] [-top toprow] [-bottom bottomrow] [-width width] [-height height] [-pad] [-verbose] [ left right width height ] [pnmfile]

xres =2048
yres = 1536


width = xres / 4
height = yres / 3

img_map = [ None, "posy", None, None, "posx", "posz", "negx", "negz", None, "negy", None, None]

infile = "skybox_texture.jpg"


for y in range(3):
  for x in range(4):
    fname = img_map[y * 4 + x]
    if fname:
      xstart = x * width
      ystart = y * height
      cmd = "djpeg %s | pamcut %d %d %d %d | cjpeg > %s.jpg" % (infile, xstart, ystart, width, height, fname)
      print cmd

