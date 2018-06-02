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
    static private String terrainDefault = "burb";
    static private String mpath = "/mnt/sunshine/vids/software/mine/tankette-maps/";

    static {
        terrainDems.put(    "smallmars", "/var/tmp/smallmars/smallmars.db");
        terrainTextures.put("smallmars", "/var/tmp/smallmars/smallmarsimages.db");
        demTables.put(      "smallmars", "terrain");
        textureTables.put(  "smallmars", "images");
        
        terrainDems.put(    "burb", "/var/tmp/tiffy/burb.db");
        terrainTextures.put("burb", "/var/tmp/tiffy/burb.db");
        demTables.put(      "burb", "terrain");
        textureTables.put(  "burb", "textures");

        terrainDems.put(    "bigmars", mpath + "terrain-mars16.db");
        terrainTextures.put("bigmars", mpath + "textures-mars16.db");  
        demTables.put(      "bigmars", "terrain");
        textureTables.put(  "bigmars", "textures");
        
        terrainDems.put(    "maze", mpath + "maze.db");
        terrainTextures.put("maze", mpath + "maze.db");  
        demTables.put(      "maze", "terrain");
        textureTables.put(  "maze", "textures"); 
        
        terrainDems.put(    "putin", mpath + "putin.db");
        terrainTextures.put("putin", mpath + "putin.db");  
        demTables.put(      "putin", "terrain");
        textureTables.put(  "putin", "textures"); 
        
        terrainDems.put(    "mblur", mpath +"mblur.db");
        terrainTextures.put("mblur", mpath +"mblur.db");  
        demTables.put(      "mblur", "terrain");
        textureTables.put(  "mblur", "textures"); 
        
        terrainDems.put(    "mdtri", mpath +"mdtri.db");
        terrainTextures.put("mdtri", mpath + "mdtri.db");  
        demTables.put(      "mdtri", "terrain");
        textureTables.put(  "mdtri", "textures");
        
        terrainDems.put(    "tface", mpath + "tface.db");
        terrainTextures.put("tface", mpath + "tface.db");  
        demTables.put(      "tface", "terrain");
        textureTables.put(  "tface", "textures");
        
        terrainDems.put(    "face", mpath + "face.db");
        terrainTextures.put("face", mpath + "face.db");  
        demTables.put(      "face", "terrain");
        textureTables.put(  "face", "textures");
        
        terrainDems.put(    "sin", mpath +"sin.db");
        terrainTextures.put("sin", mpath +"sin.db");  
        demTables.put(      "sin", "terrain");
        textureTables.put(  "sin", "textures");
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
    
    public static void addTerrain(String key, String terraindb, String texturedb,
                                  String terrainTable, String textureTable) {
        addTerrainDemPath(key, terraindb);
        addTerrainTexturePath(key, texturedb);
        addTerrainTableName(key, terrainTable);
        addTextureTableName(key, textureTable);
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
    
}
