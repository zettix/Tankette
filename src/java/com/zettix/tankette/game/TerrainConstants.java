  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of hard coded constants across the application.
 * 
 * Try to collect paths and constants here for the back end. The data itself
 * is hard coded, it is not a general object store.  This tradeoff uses
 * method names and data names to document what is going on.
 * 
 * Instead of creating a terrain configuration object, the data is simply
 * four Maps.  This, while helpful, leaves out full details, the need for
 * a manifest table in the DEM database.  Nor the format of the entries.
 * 
 * Documentation ipso facto the terrain format needs some help.  Here is the
 * gist, while belonging elsewhere.  Nevertheless, the reader may be interested
 * in resource packs.  Without further ado, the data needed for this app:
 * SQlite database terrain has two tables:
 *   table manifest has one entry, an id of manifest and a data of xdim and ydim
 *   table terrain has xdim times ydim files, of the format "data_X_Y.dat"
 *   each "dat" blob has the following format: 4 byte x, then y resolution.
 *   two byte signed integer data follows. for X by Y, this is 2 * X * Y bytes.
 *   Please note Java uses network big endian; maybe, but I had to switch using
 *   C to parse NASA tiff files.  See tankette utility programs for more info,
 *   but at the moment the source expects 32x32.
 * 
 * SQlite database for images has one table.
 *   Important: xdim and ydim are gleaned from the terrain database manifest.
 *   Therefore, the table specified should have id/blob like "image_X_Y.jpg"
 *   and the jpeg raw data as the blob.
 * 
 * Therefore a terrain is specified at present like so:
 * addTerrain(String key, String terraindb, String texturedb,
 *                                String terrainTable, String textureTable)
 * 
 * The class is static so see the initializer for examples.
 * 
 * @author sean
 */
public class TerrainConstants {
    
    static private Map<String, String> terrainDems = new HashMap<>();
    static private Map<String, String> terrainTextures = new HashMap<>();
    
    static private Map<String, String> demTables = new HashMap<>();
    static private Map<String, String> textureTables = new HashMap<>();
    static private Map<String, String> imageFmt = new HashMap<>();
    static private String terrainDefault = "vmars";
    static private String mpath = "/mnt/sunshine/vids/software/mine/tankette-maps/";
    static private String tpath = "/mnt/1T/1/sean-cache/";

    static {        
        terrainDems.put(    "burb", "/var/tmp/tiffy/burb.db"); // height x
        terrainTextures.put("burb", "/var/tmp/tiffy/burb.db");
        demTables.put(      "burb", "terrain");
        textureTables.put(  "burb", "textures");
        imageFmt.put(       "burb", "image_%s.jpg");
        
        terrainDems.put(    "blaze", "/var/tmp/tiffy/blaze.db");
        terrainTextures.put("blaze", "/var/tmp/tiffy/blaze.db");
        demTables.put(      "blaze", "terrain");
        textureTables.put(  "blaze", "textures");
        imageFmt.put(       "blaze", "tex_%s.png");
         
        terrainDems.put(    "fam", "/var/tmp/tiffy/fambig.db");
        terrainTextures.put("fam", "/var/tmp/tiffy/fambig.db");
        demTables.put(      "fam", "terrain");
        textureTables.put(  "fam", "textures");
        imageFmt.put(       "fam", "tex_%s.png");
         
        terrainDems.put(    "newb", "/var/tmp/tiffy/newbie.db");
        terrainTextures.put("newb", "/var/tmp/tiffy/newbie.db");
        demTables.put(      "newb", "terrain");
        textureTables.put(  "newb", "textures");
        imageFmt.put(       "newb", "tex_%s.png");
        
        terrainDems.put(    "neonmars", tpath + "marsneon.db");
        terrainTextures.put("neonmars", tpath + "marsneon.db");  
        demTables.put(      "neonmars", "terrain");
        textureTables.put(  "neonmars", "textures");
        imageFmt.put(       "neonmars", "tex_%s.png");       
        
        // 20000.0, 10000.0 
        terrainDems.put(    "vmars", tpath + "marsviking32.db");  // z 0.004
        terrainTextures.put("vmars", tpath + "marsviking32.db");  
        demTables.put(      "vmars", "terrain");
        textureTables.put(  "vmars", "textures");
        imageFmt.put(       "vmars", "tex_%s.png");             
        
        // 200000.0, 100000.0 . 
        terrainDems.put(    "tmars", tpath + "marsviking-1280x720.db");  // z 0.04
        terrainTextures.put("tmars", tpath + "marsviking-1280x720.db");  
        demTables.put(      "tmars", "terrain");
        textureTables.put(  "tmars", "textures");
        imageFmt.put(       "tmars", "tex_%s.png");    
        
        terrainDems.put(    "maze", mpath + "maze.db");
        terrainTextures.put("maze", mpath + "maze.db");  
        demTables.put(      "maze", "terrain");
        textureTables.put(  "maze", "textures"); 
        imageFmt.put(       "maze", "image_%s.jpg");       
        
        terrainDems.put(    "putin", mpath + "putin.db");
        terrainTextures.put("putin", mpath + "putin.db");  
        demTables.put(      "putin", "terrain");
        textureTables.put(  "putin", "textures"); 
        imageFmt.put(       "putin", "image_%s.jpg");      
        
        terrainDems.put(    "mblur", mpath +"mblur.db");
        terrainTextures.put("mblur", mpath +"mblur.db");  
        demTables.put(      "mblur", "terrain");
        textureTables.put(  "mblur", "textures"); 
        imageFmt.put(       "mblur", "image_%s.jpg");       
        
        terrainDems.put(    "mdtri", mpath +"mdtri.db");
        terrainTextures.put("mdtri", mpath + "mdtri.db");  
        demTables.put(      "mdtri", "terrain");
        textureTables.put(  "mdtri", "textures");
        imageFmt.put(       "mdtri", "image_%s.jpg");       
        
        terrainDems.put(    "sface", tpath + "smallface.db");
        terrainTextures.put("sface", tpath + "smallface.db");  
        demTables.put(      "sface", "terrain");
        textureTables.put(  "sface", "textures");
        imageFmt.put(       "sface", "tex_%s.png");       
        
        terrainDems.put(    "face", mpath + "face.db");
        terrainTextures.put("face", mpath + "face.db");  
        demTables.put(      "face", "terrain");
        textureTables.put(  "face", "textures");
        imageFmt.put(       "face", "image_%s.jpg");
        
        terrainDems.put(    "sin", mpath +"sin.db");
        terrainTextures.put("sin", mpath +"sin.db");  
        demTables.put(      "sin", "terrain");
        textureTables.put(  "sin", "textures");
        imageFmt.put(       "sin", "image_%s.jpg");
    }
    
    private static void addTerrainDemPath(String key, String dbpath) {
        terrainDems.put(key, dbpath);
    }
    
    private static void addTerrainTexturePath(String key, String dbpath) {
        terrainTextures.put(key, dbpath);
    }
    
    private static void addTerrainTableName(String key, String tableName) {
        demTables.put(key, tableName);
    }
    
    private static void addTextureTableName(String key, String tableName) {
        textureTables.put(key, tableName);
    }
    
    private static void addImageFmt(String key, String tableName) {
        imageFmt.put(key, tableName);
    }
    
    public static void addTerrain(String key, String terraindb, String texturedb,
                                  String terrainTable, String textureTable,
                                  String imageTable) {
        addTerrainDemPath(key, terraindb);
        addTerrainTexturePath(key, texturedb);
        addTerrainTableName(key, terrainTable);
        addTextureTableName(key, textureTable);
        addImageFmt(key, imageTable);
    }
    
    public static String  getDefaultTerrainKey() {
        return terrainDefault;
    }
    
    public static String getTerrainDemPath(String key) {
        if (terrainDems.containsKey(key)) {
            return terrainDems.get(key);
        }
        return null;
    }
    
    public static String getTerrainTexturePath(String key) {
        if (terrainTextures.containsKey(key)) {
            return terrainTextures.get(key);
        }
        return null;
    }
    public static String getTerrainTable(String key) {
        if (demTables.containsKey(key)) {
            return demTables.get(key);
        }
        return null;
    }

    public static String getTextureTable(String key) {
        if (textureTables.containsKey(key)) {
            return textureTables.get(key);
        }
        return null;
    }
    
    public static String getImageFmt(String key) {
                if (imageFmt.containsKey(key)) {
            return imageFmt.get(key);
        }
        return null;
    }
    
}
