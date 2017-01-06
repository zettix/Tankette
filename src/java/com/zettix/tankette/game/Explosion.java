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
public class Explosion extends Model implements ModelInterface {
    // An explosion has the following attributes:
    // Time length
    // max scale
    // End scale
    // It uses, say, the positive part of the sine function,
    // to create a growth and decline back to 0.
    private long start_time = 0l;
    private long explosion_length = 1l;
    private double max_scale = 1.0;
    private boolean done = false;

    public Explosion(long now, long boom_long, double maxie) {
        explosion_length = boom_long;
        max_scale = maxie;
        start_time = now;
    }

    public boolean isDone() {
        return done;
    }
    
    public void Update(long now) {
        long delta = now - start_time;
        if (delta > explosion_length) {
            done = true;
        } else {
            double s = Math.PI;
            s = s * (float) delta / (float) explosion_length;
            s = Math.sin(s) * max_scale;
        }
    }
}
