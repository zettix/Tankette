/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

// TerrainManager
//   Serve terrain tiles based on location.
//
// Resources: textures of form image_x_y.jpg for tile x, y
//            double data of form data_x_y.dat for tile x, y
// Output: for given geometric point P, return some data with geometry and texture from resources.
//          The texture is for the client.
// Load(): load resources.
// List<names> getTilesForPoint(double x, double y);
//  be lazy.  Do not load unless asked.

// import com.zettix.rocketsocket.RocketConstants;
import com.zettix.graphics.gjkj.util.V3;
import com.zettix.tankette.game.interfaces.AbstractTerrain;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import org.apache.log4j.*;

public final class Terrain extends AbstractTerrain {

   private static final String RESOURCE_PATH = "/terrain";
   private static final String MANIFEST = "files";
   private static final Pattern DATA_RE = Pattern.compile(
       "data_(\\d+)_(\\d+).dat");
   private final Logger LOG = Logger.getLogger(Terrain.class);
   private final int maxTiles = 127;  // tune this.
   private final Map<String, AbstractTerrain> tiles; // LRU
   private double xRoot = 0.0;
   private double yRoot = 0.0;
   private double zRoot = 0.0;
   private double xDim = 0.0;
   private double yDim = 0.0;
   private int xTileCount = 0;
   private int yTileCount = 0;
   private double xTileWidth = 0.0;
   private double yTileWidth = 0.0;
   private double xTilesPerDim = 0.0;
   private double yTilesPerDim = 0.0;
   private DataBaseHandler dataBaseHandler;
   
   public Terrain() {
    this.tiles = new LRU<>(25, 0.75f, maxTiles);
   }

   public AbstractTerrain setSize(double x, double y) {
       this.xDim = x;
       this.yDim = y;
       return this;
   }
   
   public AbstractTerrain setPosition(V3 p) {
       this.xRoot = p.coords[0];
       this.yRoot = p.coords[1];
       this.zRoot = p.coords[2];       
       return this;
   }
   
   private String GetShortName(int ax, int ay) {
     //System.out.println("Getting shortie for " + ax + " and " + ay);
     if (ax < 0 || ay < 0 || ax > xTileCount || ay > yTileCount) {
         //System.out.println("I don't think so! " + ax + " limit " + xTileCount + " , " + ay + " limit " + yTileCount);
         return null;
     }
     return String.format("%d_%d", ax, ay);
   }

   /** Get information about tiles without loading any.
    *
    *  Given a directory, the RESOURCE_PATH, files matching *.dat
    *  are parsed with DATA_RE extracting x and y values from
    *  files like data_33_2.dat
    *  Maximum x and y define the tile space, which must be rectangular.
    *  individual data files can vary their rectangular resolution
    *  independently, and are not loaded until needed.
   */
   public void ConnectDatabase(String databaseKey) {
    String dbKey = TerrainConstants.getDefaultTerrainKey();
    if (databaseKey != null && databaseKey.length() > 0) {
        dbKey = databaseKey;
    }
    dataBaseHandler = new DataBaseHandler(
            TerrainConstants.getTerrainDemPath(dbKey));
           
    dataBaseHandler.Connect();
    dataBaseHandler.getManifest();
    int dx = dataBaseHandler.getX();
    int dy = dataBaseHandler.getY();
    System.out.println("Database Loaded with X: " + dx + " and Y: " + dy );
    System.out.println("Loading the terrain manager");
    xTileCount = dx;
    yTileCount = dy;
    xTileWidth = (double) xDim / (double) (xTileCount); // start w/ 0
    yTileWidth = (double) yDim / (double) (yTileCount); // start w/ 0
    xTilesPerDim =  (double) (xTileCount) / (double) (xDim);
    yTilesPerDim =  (double) (yTileCount) / (double) (yDim);
    System.out.println(toString());
  }
 
  protected AbstractTerrain FullLoad(String tilename) {
    boolean dbLoad = true;
    System.out.println("Full load of " + tilename + " For a total of tiles: " + tiles.size());
    String path = "data_" + tilename + ".dat";
    String fullpath = RESOURCE_PATH + "/" + path;
    Matcher m = DATA_RE.matcher(path);
    if (m.matches() == true)  {
      int px = Integer.parseInt(m.group(1));
      int py = Integer.parseInt(m.group(2));
      double xx = (double) (px) * xTileWidth + xRoot;
      double yy = (double) (py) * yTileWidth + zRoot;
      //  public Tile(double x, double y, double z, double rx, double ry, int c,
      //        String url, String name, double[] indata)
      //String url = String.format("/BoxMove/images/image_%d_%d.jpg", px, py);
      // With the database endpoint:
      //     localhost:8080/BoxMove/ImageServelet?image=image_0_9.jpg
      String url = String.format("/Tankette/ImageServelet?image=image_%d_%d.jpg", px, py);
      try {
        InputStream stream;
        if (dbLoad) {
            stream = new ByteArrayInputStream(dataBaseHandler.getBlob("terrain", path));
        } else {
            stream = Terrain.class.getClassLoader().getResourceAsStream(fullpath);
        }
        DataInputStream f = new DataInputStream(stream);
        int count = f.readInt();
        int unusedYcount = f.readInt();
        // System.out.println("Loading tile: " + tilename + " c: " + count);
        double[] d = new double[count * count];
        for (int j = 0; j < count * count; j++) {
            // Important change.  Database is now int16s.
            d[j] = ((double) f.readShort());
            
            // d[j] = (double) f.readFloat() * 0.9f;  // scaled down?
        }
        /* System.out.println("  xx:" + xx +
                           "\n  yy:" + yy +
                           "\n  z:" + z +
                           "\n  xTileWidth:" + xTileWidth +
                           "\n  yTileWidth:" + yTileWidth +
                           "\n  count:" + count +
                           "\n  url:" + url +
                           "\n  tilename:" + tilename +
                           "\n  d:" + Arrays.toString(d)); */
        AbstractTerrain t = new TerrainTile(xx, yRoot, yy, xTileWidth, yTileWidth, count, url,
                          tilename, d);
        tiles.put(tilename, t);
        return t;
      } catch (IOException e) {
        System.out.println("Could not get tile! " + e);
      }
    }
    System.out.println("Tile load failed, no match? " + tilename);
    return null;
  }

  public String GetTileName(double xi, double yi) {
    // validate
    if ( (xi - xRoot) < 0.0 || (yi - zRoot)  < 0.0 || (xi - xRoot) > xDim || (yi - zRoot) > yDim ) {
      return null;
    }
    double xx = (double) (xi - xRoot) * xTilesPerDim;
    double yy = (double) (yi - zRoot) * yTilesPerDim;
    int tx = (int) xx;
    int ty = (int) yy;
    return GetShortName(tx, ty);
  }
    
  public AbstractTerrain GetTile(double xi, double yi) {
    // System.out.println("Getting tile at: " + xi +  ", " + yi);
    String name =  GetTileName(xi, yi);
    // System.out.println("name" + name);
    return GetTile(name);
  }

  public AbstractTerrain GetTile(String tilename) {
    //System.out.println("Getting tile name: " + tilename);
    //System.out.println("All keys: " + tiles.keySet());
    // System.out.println("And is this key: " + tilename + " in the keyset?" + tiles.containsKey(tilename) + " is your ans");
    if (tilename == null) return null;
    
    AbstractTerrain t = null;
    if (tiles.containsKey(tilename)) {
      t = tiles.get(tilename);
    } else {
      tiles.put(tilename, t);
    }
    // System.out.println("here is teh tiule: " + t);
    if (t == null) {
        return FullLoad(tilename);
    } else {
        return t;
    }
  }

  public double getHeight(double xi, double yi) {
    AbstractTerrain t = GetTile(xi, yi);
    if (null == t) {
        return 0.0f;
    }
    return t.getHeight(xi, yi);
  }

  public List<String> GetTileNamesFor(double xi, double yi, int radius) {
    // validate
    List<String> names = new ArrayList<>();
    if ( (xi - xRoot) < 0.0 || (yi - zRoot)  < 0.0 || (xi - xRoot) > xDim || 
            (yi - zRoot) > yDim  || radius < 1) {
        return names;
    }
    double xx = (double) (xi - xRoot) * xTilesPerDim;
    double yy = (double) (yi - zRoot) * yTilesPerDim;
    int tx = (int) xx;
    int ty = (int) yy;
    for (int j = -radius + 1 ; j < radius ; j++) {
      for (int i = -radius + 1 ; i < radius ; i++) {
        if ((i * i) + (j * j) > (radius * radius)) continue;
        String tilename = GetShortName(tx + i, ty + j);
        if (tilename != null) {
            names.add(tilename);
        }
      }
    }
    return names;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String nn = "\n";
    sb.append("Terrain Server, at your service!\n")
        .append("\n")
        .append("X pos: ")
        .append(xRoot)
        .append(nn)
        .append("Y pos: " + yRoot + nn)
        .append("Z pos: " + zRoot + nn)
        .append("xTileCount: " + xTileCount + nn)
        .append("yTileCount: " + yTileCount + nn)
        .append("xDim: " + xDim + nn)
        .append("yDim: " + yDim + nn)
        .append("Tiles: " + tiles.size() + nn);
    
    int count = 0;
    for (Map.Entry<String, AbstractTerrain> entry : tiles.entrySet()) {
      sb.append(entry.getKey() + " -> " + entry.getValue());
      if  (count == 5) {
        sb.append("\n");
        count = 0;
      } else {
        sb.append(", ");
        count++;
      }
    }
    if (count != 6) sb.append("\n");
    return sb.toString();
  }

  // testing.
  public static void main(String... argv) {
    Terrain ts = new Terrain();
    System.out.println(ts.toString());
    System.out.println(ts.GetTile(5.0f, 5.0f));
    List<String> names = ts.GetTileNamesFor(5.0f, 5.0f, 1);
    System.out.println("Better get " + names.size());
    for (String s : names) {
      System.out.println("  get: " + s);
      AbstractTerrain t = ts.GetTile(s);
      System.out.print("Here he is: " + t);
    }
    System.out.println(ts.toString());
      for (double f = 0.0f; f < 9.0f; f += .10f) {
      double ix = f * 1.0f;
      double iy = f * .4f;
      System.out.println("Traviling to " + ix + ", " + iy);
      System.out.println("Height: " + ts.getHeight(ix, iy));
    }
  }
}
