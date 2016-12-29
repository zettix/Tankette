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
public class Turdle {
    private double x, y, z, xr, yr, zr, scale;
    private String id;
    public final double radius;
    public int age;
    
    public final double rotation_speed;
    public String getId() {return id;}
    public double getX() { return x;}
    public double getY() { return y;}
    public double getZ() { return z;}
    public double getXr() { return xr;}
    public double getYr() { return yr;}
    public double getZr() { return zr;}
    public double getScale() { return scale;}
    
    public void setScale(double f) { this.scale = f;}
    public void setX(double f) {  this.x = f;}
    public void setY(double f) {  this.y = f;}
    public void setZ(double f) {  this.z = f;}
    public void setXr(double f) {  this.xr = f;}
    public void setYr(double f) {  this.yr = f;}
    public void setZr(double f) {  this.zr = f;}
    public void setId(String s) { this.id = s;}
    public Turdle() {
        radius = 3.0f;
        rotation_speed = 0.1f;
        age = 0;
        scale = 1.0;
    }
}
