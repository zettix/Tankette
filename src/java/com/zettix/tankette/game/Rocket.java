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
public class Rocket extends Model {
    
    private long max_age = 1000l;  // one second
    public long getMax_age() {return max_age;}
    public void setMax_age(long max_age) {this.max_age = max_age;}
        
    public Rocket(long now, long maxie) {
        setStartTime(now);
        setAgo(now);
        max_age = maxie;
    }
    
    @Override
    public void Update(long now) {
        long delta = now - getStartTime();
        if (delta > max_age) {
            setDone();
        } else {
            MoveForward(delta);
        }
    }
    
    public Rocket() {
        long now  = System.currentTimeMillis();
        setStartTime(now);
        setAgo(now);
        setRadius(3.0);
        s = 1.0;
        Hitbox h = new Hitbox(this);
        
        // Current model, from rocket.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(6, 1.1, 1.1);
        // 
        V3 dim = new V3(6.1, 1.1, 1.1);
        M4 mover = new M4().Identity().Move(-3.0, -0.6, -0.6);
        h.boxHull = new BoxHull(dim);
        h.boxHull.TransformObjectSpace(mover);
        setHitbox(h);
        setVelocity(.00002);
        setRotation_speed(0.01);
        setForward(true);
        setBack(false);
        setLeft(false);
        setRight(false);
        setMoved(true);
        max_age = 10000;  // ten seconds for a default.
        
    }
}
