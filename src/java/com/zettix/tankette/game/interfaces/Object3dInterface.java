/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game.interfaces;

import com.zettix.tankette.game.Object3D;

/**
 * 3-D point (x,y,z) with Euler angles (xr, yr, zr), and scale s.
 * 
 * Serves only to hold parameters, perhaps parent should be added,
 *   but that may happen at a higher level, drilling down when needed.
 *
 * @author sean
 */
public interface Object3dInterface {
    public String getId();
    public double getX();
    public double getY();
    public double getZ();
    public double getXr();
    public double getYr();
    public double getZr();
    public double getScale();
    public void setX(double f);
    public void setY(double f);
    public void setZ(double f);
    public void setXr(double f);
    public void setYr(double f);
    public void setZr(double f);
    public void setId(String s);
    public void setScale(double f);
    public Object3D copy(Object3D copyFrom);
}
