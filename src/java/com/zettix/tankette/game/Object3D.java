/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

/**
 * This is a single 3D point with an orientation and scale, that is, xyz
 * rx ry rz and scale, euler rotations but maybe quaternions once guided
 * missiles become a thing.
 * 
 * @author sean
 */
public class Object3D {
    protected double x = 0.0;
    protected double y = 0.0;
    protected double z = 0.0;
    protected double xr = 0.0;
    protected double yr = 0.0;
    protected double zr = 0.0;
    protected double s = 1.0;
    private String id;
    public String getId() {return id;}
    public double getX() { return x;}
    public double getY() { return y;}
    public double getZ() { return z;}
    public double getXr() { return xr;}
    public double getYr() { return yr;}
    public double getZr() { return zr;}
    public double getScale() { return s;}
    public void setX(double f) {  this.x = f;}
    public void setY(double f) {  this.y = f;}
    public void setZ(double f) {  this.z = f;}
    public void setXr(double f) {  this.xr = f;}
    public void setYr(double f) {  this.yr = f;}
    public void setZr(double f) {  this.zr = f;}
    public void setId(String s) { this.id = s;}
    public void setScale(double f) {this.s = f;}
}
