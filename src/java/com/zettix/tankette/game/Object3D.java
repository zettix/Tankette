/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.graphics.gjkj.util.V3;
import com.zettix.tankette.game.interfaces.Object3dInterface;

/**
 * This is a single 3D point with an orientation and scale, that is, xyz
 * rx ry rz and scale, euler rotations but maybe quaternions once guided
 * missiles become a thing.
 * 
 * @author sean
 */
public class Object3D implements Object3dInterface {
    protected V3 p;
    protected V3 r;
    protected double s = 1.0;
    private String id;
    public String getId() {return id;}
    public double getX() { return p.coords[0];}
    public double getY() { return p.coords[1];}
    public double getZ() { return p.coords[2];}
    public double getXr() { return r.coords[0];}
    public double getYr() { return r.coords[1];}
    public double getZr() { return r.coords[2];}
    public double getScale() { return s;}
    public void setX(double f) {  p.coords[0] = f;}
    public void setY(double f) {  p.coords[1] = f;}
    public void setZ(double f) {  p.coords[2] = f;}
    public void setXr(double f) { r.coords[0] = f;}
    public void setYr(double f) { r.coords[1] = f;}
    public void setZr(double f) { r.coords[2] = f;}
    public void setId(String s) { id = s;}
    public void setScale(double f) {this.s = f;}

    public Object3D(double x, double y, double z, 
                    double rx, double ry, double rz,
                    double scale, String name) {
        p = new V3(x, y, z);
        r = new V3(rx, ry, rz);
        s = scale;
        id = name;
    }
    
     public Object3D() {
        p = new V3();
        r = new V3();
        s = 1.0;
        id = "None";
    }
     
    public void clampToTerrain(double groundlevel, V3 groundNormal) {
       setY((double) groundlevel + 1.0);   
       
    }
    
    public Object3D copy(Object3D copyFrom){
        // B O R I N G
        // O
        // R  (trust not
        // I   the clone)
        // N
        // G
        p.coords[0] = copyFrom.p.coords[0];
        p.coords[1] = copyFrom.p.coords[1];
        p.coords[2] = copyFrom.p.coords[2];
        r.coords[0] = copyFrom.r.coords[0];
        r.coords[1] = copyFrom.r.coords[1];
        r.coords[2] = copyFrom.r.coords[2];
        s = copyFrom.s;
        id = copyFrom.id;
        return this;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Ob:[")
            .append(p.coords[0]).append(",")
            .append(p.coords[1]).append(",") 
            .append(p.coords[2]).append("] <")
            .append(r.coords[0]).append(",")
            .append(r.coords[1]).append(",")
            .append(r.coords[2]).append(">");        
        return sb.toString();
    }
    
    // Transform 6 points (collision box) to player space
    // for axis-aligned tests.
    public void Invert(double[] points) {
        // first translate:
        points[0] -= p.coords[0];
        points[1] -= p.coords[0];
        points[2] -= p.coords[1];
        points[3] -= p.coords[1];
        points[4] -= p.coords[2];
        points[5] -= p.coords[2];
        // maybe z/y/x?
        // TODO rotations.    
    }
    
    public void Transform(double[] points) {
        // first translate:
        points[0] += p.coords[0];
        points[1] += p.coords[0];
        points[2] += p.coords[1];
        points[3] += p.coords[1];
        points[4] += p.coords[2];
        points[5] += p.coords[2];
        // maybe z/y/x?
        // TODO rotations.
    }
}