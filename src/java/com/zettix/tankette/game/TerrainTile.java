/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

/**
 *
 * @author sean
 */
import com.zettix.graphics.gjkj.util.V3;
import com.zettix.tankette.game.interfaces.AbstractTerrain;
import java.text.DecimalFormat;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;


public final class TerrainTile extends AbstractTerrain {
  V3 p;              
  public final double rx;              
  public final double ry;              
  private final double rx_1;
  private final double ry_1;
  public final int c;              
  public final String url;              
  public final String name;              
  public final double[] data;              
  public JsonObject json;
  private long lastAccess;
  private static double last = 0.0f;
  private final String TTF = "%3.2f";
  public final static DecimalFormat FIVE_TWO_FORMAT = new DecimalFormat("##0.00");

  public TerrainTile(final double x,final  double y,final double z, final double rx,
              final double ry, final int c, final String url, final String name,
              final double[] indata) {
     this.p = new V3(x, y, z);         
     this.rx = rx;              
     this.ry = ry;              
     this.c = c;              
     this.url = url;              
     this.name = name;              
     this.data = indata;
     this.json = null;
     rx_1 = 1.0f / rx;
     ry_1 = 1.0f / ry;
     toJson();
     lastAccess = System.currentTimeMillis();
     // System.out.println("ME! "  + this);
  }
  
  @Override
  public String toString() {
      StringBuilder sb = new StringBuilder()
              .append("Tile: ")
              .append(name)
              .append("\nX: ")
              .append(p.coords[0])
              .append("\nY: ")
              .append(p.coords[1])
              .append("\nZ: ")
              .append(p.coords[2])
              .append("ResX:")
              .append(rx)
              .append("ResY")
              .append(ry) 
              .append("\nC: ")
              .append(c)              
              .append("\nU: ")
              .append(url)
              .append(""
                      +"\n");
      for (int yy = 0; yy < c; yy++) {
          for (int xx = 0; xx < c; xx++) {
              sb.append(FIVE_TWO_FORMAT.format(data[yy * c + xx]))
                      .append(" ");
          }
          sb.append("\n");
      }
      return sb.toString();
  }

  public double getHeight(double xi, double yi) {
    // simple bilinear interpolation.  Hate divide but sometimes you gotta.
    // first find floor cell.  int of position into matrix.
    // each cell of data[c][c] is 1/rx 1/ry big. so which is c -> rx
    // for xi, and c -> ry for y.  so 0 < (xi - x)  < rx.
    //                                  0 < (xi -x ) / rx < 1.0
    //       cellX0 = (int) (double) c * (xi - x) / rx;
    //       cellY0 = (int) (double) c * (yi - y) / ry;
    //       cellFX0 = c * (xi - x) / rx - (double) (cellX0);
    //       cellFY0 = c * (xi - x) / rx - (double) (cellX0);
    //       cellX1 = cellX0 + 1;
    //       cellY1 = cellY0 + 1;
    //       answer is now data[cellX0] * cellFX0 * 0.25 
    //                   + data[cellY0] * cellFY0 * 0.25      
    //                   + data[cellX0] * (1.0 - cellFX0) * 0.25
    //                   + data[cellY0] *  (1.0 -cellFY0) * 0.25      
    //

    double x0 = (xi - p.coords[0]) * rx_1;   // (xi - x) / rx -> 0...1
    double y0 = (yi - p.coords[2]) * ry_1;   // (yi - y) / ry -> 0...1
    double cell_width = (double) (c -1);
    double cx0 = (double) cell_width * x0; // [0...c, 0...c]
    double cy0 = (double) cell_width * y0;

    int icx0 = (int) cx0;   // floor of cx
    int icy0 = (int) cy0;

    double px = cx0 - (double) icx0;  // percent of cx 0..1
    double py = cy0 - (double) icy0;  // percent of cy 0..1
    double px1 = 1.0f - px;
    double py1 = 1.0f - py;
    int i00 = icx0 + icy0 * c;
    int i10 = icx0 + 1 + icy0 * c;
    int i01 = icx0 + (icy0 + 1) * c;
    int i11 = icx0 + 1 + (icy0 + 1) * c;
    double scale = 1.00f;
    double v = (data[i00] * (px1 * py1) +  // 00
               data[i01] * (px1 * py) +  // 01
               data[i10] * (px  * py1 ) +  // 10
               data[i11] * (px  * py )) * scale;  // 11
    if (v != last) {
        last = v;
        StringBuffer sb = new StringBuffer("\nREEEEEEEEEE ")
                .append(this.name)
                .append(String.format(" xi: %3.2f", xi))
                .append(String.format(" yi: %3.2f", yi))
                .append(String.format("datacell( %d", icx0))
                .append(String.format(", %d)[%d x %d]", icy0, c, c))       
                .append(String.format(" a[00][%d] %3.2f", i00, data[i00]))
                .append(String.format(" a[01][%d] %3.2f", i01, data[i01]))
                .append("\n")
                .append(String.format(" a[10][%d] %3.2f", i10, data[i10]))
                .append(String.format(" a[11][%d] %3.2f", i11, data[i11]))
                .append("\n")
                //.append(String.format("uuu", args))
                .append(String.format(" px: %3.2f", px))
                .append(String.format(" py: %3.2f", py))
                .append(String.format(" px1: %3.2f", px1))
                .append(String.format(" py1: %3.2f", py1)) 
                //.append(" s00: " + data[i00] * (px1 * py1) * scale)  // 00
                //.append(" s01: " + data[i01] * (px  * py1) * scale)  // 01
                //.append(" s10: " + data[i10] * (px1 * py ) * scale) // 10
                //.append(" s11: " + data[i11] * (px  * py ) * scale)  // 11
                .append(String.format(" v: %3.2f", v));
     
        double g = v - last;
        // g = g * g;
        if ( g * g > 0.01) {
            sb.append("Shit shit shit " + g);
        }
        //System.out.println(sb.toString());
    }
    return v;
  }
  
  // normal uses cross product.  

  public JsonObject toJson() {
     // lazy.
     lastAccess = System.currentTimeMillis();
     if (json == null) {
       // composing json.
       JsonProvider provider = JsonProvider.provider();
       JsonArrayBuilder b = provider.createArrayBuilder();
       for (double f : data) {
         b.add(f);
       }
       json = provider.createObjectBuilder()
                  .add("n", name)
                  .add("x", p.coords[0])
                  .add("y", p.coords[2]) // NOTE threejs Plane Geo is XY(Z=0).
                  .add("z", p.coords[1]) // So we swap Y/Z here and rotate 90 in the client.
                  .add("rx", rx)
                  .add("ry", ry)
                  .add("c", c)
                  .add("u", url)
                  .add("d", b)
                  .build();
     }
     // System.out.println("I love you! " + this);
     return json;
  }
}