// TerrainServer
//   Serve terrain tiles based on location.
//
// Resources: textures of form image_x_y.jpg for tile x, y
//            float data of form data_x_y.dat for tile x, y
// Output: for given geometric point P, return some data with geometry and texture from resources.
//          The texture is for the client.
// Load(): load resources.
// List<names> getTilesForPoint(float x, float y);
//  be lazy.  Do not load unless asked.

package terrain;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;

class TerrainServer {

   public static final String BASE_A_LOT =
       "0123456789" + // 1
       "abcdefghij" + // 2
       "klmnopqrst" + // 3
       "uvwxyzABCD" + // 4
       "EFGHIJKLMN" + // 5
       "OPQRSTUVWX" + // 6
       "YZ!@#$%^&*" + // 7
       "()-_=+:;,." + // 8
       "`~";  // 82.  82 is closest to 64, not 128. but 64x64 is a lot.

   private static final String RESOURCE_PATH = "meshes";
   private static final Pattern DATA_RE = Pattern.compile(
       "data_(\\d+)_(\\d+).dat");
   private final Map<String, Tile> tiles = new HashMap<>();
   private final Map<String, String> datapaths = new HashMap<>();
   private float x = 0.0f;
   private float y = 0.0f;
   private float z = 0.0f;
   private float xDim = 10.0f;
   private float yDim = 10.0f;
   private int xTileCount = 0;
   private int yTileCount = 0;
   private float xTileWidth = 0.0f;
   private float yTileWidth = 0.0f;
   private float xTilesPerDim = 0.0f;
   private float yTilesPerDim = 0.0f;
   
   public TerrainServer() {
    SoftLoad();
   }

   public static String GetShortName(int ax, int ay) {
     // TODO(sean):assert ax, ay < BASE_A_LOT.length
     return String.format("%c%c", BASE_A_LOT.charAt(ax), BASE_A_LOT.charAt(ay));
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
   private void SoftLoad() {
    // TODO(sean): add cache expiry.  The loaded tiles are a cache.
    Path rpath =  Paths.get(RESOURCE_PATH);
    DirectoryStream<Path> stream = null;
    // System.out.println("Looking in " + rpath);
    try {
      stream = Files.newDirectoryStream(rpath, "*.dat");
    } catch (IOException | DirectoryIteratorException e) {
      System.out.println("Directory Exception! " + e.getCause());
      return;
    }
    for (Path entry: stream) {
       String filename = entry.getFileName().toString();
       Matcher m = DATA_RE.matcher(filename);
       if (m.matches() == true)  {
         int px = Integer.parseInt(m.group(1));
         int py = Integer.parseInt(m.group(2));
         String shortname = GetShortName(px, py);
         tiles.put(shortname, null);  // lazy.
         // System.out.println("D:" + filename +  " x:" + px + " y:" + py);
         //System.out.println("D: " + shortname);
         if (px > xTileCount) {
           xTileCount = px;
         }
         if (px > yTileCount) {
           yTileCount = py;
         }
         datapaths.put(shortname, filename);
       }
    }
    xTileWidth = (float) xDim / (float) (xTileCount + 1.0f); // start w/ 0
    yTileWidth = (float) yDim / (float) (yTileCount + 1.0f); // start w/ 0
    xTilesPerDim =  (xTileCount + 1.0f) / (xDim + x);
    yTilesPerDim =  (yTileCount + 1.0f) / (yDim + y);
  }

  protected Tile FullLoad(String tilename) {
    String path = datapaths.get(tilename);
    String fullpath = RESOURCE_PATH + "/" + path;
    Matcher m = DATA_RE.matcher(path);
    if (m.matches() == true)  {
      int px = Integer.parseInt(m.group(1));
      int py = Integer.parseInt(m.group(2));
      float xx = (float) (px) * xTileWidth + x;
      float yy = (float) (py) * yTileWidth + y;
      //  public Tile(float x, float y, float z, float rx, float ry, int c,
      //        String url, String name, float[] indata)
      String url = String.format("/images/img_%d_%d.jpg", px, py);
      try {
        DataInputStream f = new DataInputStream(new FileInputStream(fullpath));
        int count = f.readInt();
        // System.out.println("Loading tile: " + tilename + " c: " + count);
        float[] d = new float[count * count];
        for (int j = 0; j < count * count; j++) {
          d[j] = (float) f.readFloat();
        }
        System.out.println("  xx:" + xx +
                           "\n  yy:" + yy +
                           "\n  z:" + z +
                           "\n  xTileWidth:" + xTileWidth +
                           "\n  yTileWidth:" + yTileWidth +
                           "\n  count:" + count +
                           "\n  url:" + url +
                           "\n  tilename:" + tilename +
                           "\n  d:" + d);
        Tile t = new Tile(xx, yy, z, xTileWidth, yTileWidth, count, url,
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

  public String GetTileName(float xi, float yi) {
    // validate
    if (x + xi < 0.0 || y + yi < 0.0 || x + xi > xDim || y + yi > yDim ) {
      return null;
    }
    float xx = (float) (xi + x) * xTilesPerDim;
    float yy = (float) (yi + y) * yTilesPerDim;
    int tx = (int) xx;
    int ty = (int) yy;
    return GetShortName(tx, ty);
  }
    
  public Tile GetTile(float xi, float yi) {
    System.out.println("Getting tile at: " + xi +  ", " + yi);
    String name =  GetTileName(xi, yi);
    System.out.println("name" + name);
    return GetTile(name);
  }

  public Tile GetTile(String tilename) {
    System.out.println("Getting tile name: " + tilename);
    if (tiles.containsKey(tilename)) {
      Tile t = tiles.get(tilename);
      if (t == null) {
        return FullLoad(tilename);
      } else {
        return t;
      }
    }
    return null;
  }

  public float GetHeight(float xi, float yi) {
    Tile t = GetTile(xi, yi);
    return t.GetHeight(xi, yi);
  }

  public List<String> GetTileNamesFor(float xi, float yi, int radius) {
    // validate
    if (x + xi < 0.0 || y + yi < 0.0 || x + xi > xDim || y + yi > yDim
        || radius < 1) {
      return null;
    }
    List<String> names = new ArrayList<>();
    float xx = (float) (xi + x) * xTilesPerDim;
    float yy = (float) (yi + y) * yTilesPerDim;
    int tx = (int) xx;
    int ty = (int) yy;
    for (int j = -radius + 1 ; j < radius ; j++) {
      for (int i = -radius + 1 ; i < radius ; i++) {
        names.add(GetShortName(tx + i, ty + j));
      }
    }
    return names;
  }

  public String GetStatus() {
    StringBuilder sb = new StringBuilder();
    sb.append("Terrain Server, at your service!\n");
    int count = 0;
    for (Map.Entry<String, Tile> entry : tiles.entrySet()) {
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
    TerrainServer ts = new TerrainServer();
    System.out.println(ts.GetStatus());
    System.out.println(ts.GetTile(5.0f, 5.0f));
    List<String> names = ts.GetTileNamesFor(5.0f, 5.0f, 1);
    System.out.println("Better get " + names.size());
    for (String s : names) {
      System.out.println("  get: " + s);
      Tile t = ts.GetTile(s);
      System.out.print("Here he is: " + t);
    }
    System.out.println(ts.GetStatus());
      for (float f = 0.0f; f < 9.0f; f += .10f) {
      float ix = f * 1.0f;
      float iy = f * .4f;
      System.out.println("Traviling to " + ix + ", " + iy);
      System.out.println("Height: " + ts.GetHeight(ix, iy));
    }
  }
}
