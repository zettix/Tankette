/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.graphics.gjkj.hull.BoxHull;
import com.zettix.graphics.gjkj.util.M4;
import com.zettix.graphics.gjkj.util.V3;

/**
 *
 * @author sean
 */
public class Rocket extends Model implements ModelInterface {
    
    public long age = 0;
    private long start_time = 0l;
    
    private long max_age = 1000l;  // one second
    public long getMax_age() {return max_age;}
    public void setMax_age(long max_age) {this.max_age = max_age;}
    
    private boolean done = false;
    
    public Rocket(long now, long maxie) {
        start_time = now;  // System millis.
        max_age = maxie;
    }
    
    @Override
    public boolean isDone() {
        return done;
    }
    
    @Override
    public void Update(long now) {
        long delta = now - start_time;
        if (delta > max_age) {
            done = true;
        } else {
            MoveForward(delta);
        }
    }
    
    public Rocket() {
        start_time = System.currentTimeMillis();
        radius = 3.0f;
        s = 1.0;
        hitbox = new Hitbox(this);
        // Current model, from rocket.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(6, 1.1, 1.1);
        // 
        V3 dim = new V3(6.1, 1.1, 1.1);
        M4 mover = new M4().Identity().Move(-3.0, -0.6, -0.6);
        hitbox.boxHull = new BoxHull(dim);
        hitbox.boxHull.TransformObjectSpace(mover);
        velocity = .000002f;
        rotation_speed = 0.01f;
        forward = true;
        back = false;
        left = false;
        right = false;
        moved = true;
        max_age = 10000;  // ten seconds for a default.
        
    }
}
