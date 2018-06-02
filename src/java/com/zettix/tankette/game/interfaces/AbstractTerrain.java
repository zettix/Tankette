/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game.interfaces;

import com.zettix.graphics.gjkj.util.V3;
import java.util.List;
import javax.json.JsonObject;

/**
 *
 * @author sean
 */


public abstract class AbstractTerrain {

    /** Get height at specified location.
     * 
     * Note this is normally X, Z in (X,Y,Z) 3D to (X,Y) 2D lookup.
     * Physical Z becomes logical Y since the terrain is 2D.
     *
     * @param x X location.
     * @param y Y location.
     * @return Height of terrain at location specified.
     */
    public double getHeight(double x, double y) {
        throw new UnsupportedOperationException();
    };

    /** Get non-unit normal to surface of terrain at location specified.
     *
     * @param x X location.
     * @param y Y location.
     * @return V3 normal to terrain at location specified.
     */
    public V3 getNormal(double x, double y) {
        throw new UnsupportedOperationException();
    }  

    /** Set the X-Y dimensions of the terrain.
     *
     * @param x dimension.
     * @param y dimension.
     * @return this object for chaining commands.
     */
    public AbstractTerrain setSize(double x, double y) {
        throw new UnsupportedOperationException();
    }
    
    /** Set 3-D Position of terrain.
     *
     * @param p (x,y,z) location of vertex(0,0) of terrain.
     * @return
     */
    public AbstractTerrain setPosition(V3 p) {
        throw new UnsupportedOperationException();
    }
    
    
    /** Connect to terrain database(s).
     *
     *  @see TerrainConstants.java
     * 
     *  @param databaseKey Key of database configuration from constants.
     */
    public void ConnectDatabase(String databaseKey) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param level Mipmap level of terrain stored on database. Head only.
     * @param databasePath filesystem/uri to mysql database.
     * @return this object for chaining commands.
     */
    public AbstractTerrain setDataBase(int level, String databasePath) {
        throw new UnsupportedOperationException(); 
    }

    /**
     *
     * @return JSON of data representing mesh and texture. Leafs only.
     */
    public JsonObject toJson() {
        throw new UnsupportedOperationException(); 
    }
    
    public AbstractTerrain GetTile(double xi, double yi) {
            throw new UnsupportedOperationException(); 
    }

    public AbstractTerrain GetTile(String tilename) {                 
      throw new UnsupportedOperationException();  
    }
  
    public List<String> GetTileNamesFor(double xi, double yi, int radius) {
        throw new UnsupportedOperationException();  
    } 
}