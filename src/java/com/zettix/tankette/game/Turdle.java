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
public class Turdle extends Model {
    private double scale;
    private String id;
    public int age;
    
    public void setScale(double f) { this.scale = f;}
    public double getScale() { return this.scale;}

    public Turdle() {
        radius = 3.0f;
        rotation_speed = 0.1f;
        age = 0;
        scale = 1.0;
    }
}
