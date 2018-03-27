/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;
import java.lang.Math.*;
/**
 *
 * @author sean
 */
public class Explosion extends Model {
    // An explosion has the following attributes:
    // Time length
    // max scale
    // End scale
    // It uses, say, the positive part of the sine function,
    // to create a growth and decline back to 0.
    private long start_time = 0l;
    private long explosion_length = 1l;
    private double max_scale = 1.0;

    public Explosion(long now, long boom_long, double maxie) {
        explosion_length = boom_long;
        max_scale = maxie;
        start_time = now;
    }

    @Override
    public void Update(long now, double delta) {
        long age = now - start_time;
        if (age > explosion_length) {
            setDone();
        } else {
            double sx = Math.PI;
            sx = sx * (float) age / (float) explosion_length;
            sx = Math.sin(sx) * max_scale;
            setScale(sx);
        }
    }
}
