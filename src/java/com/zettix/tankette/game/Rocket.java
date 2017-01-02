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
    
    public long age = 0;
    
    public Rocket() {        
        radius = 3.0f;
        hitbox = new Hitbox(this);
        // Current model, from rocket.js:
        //   this.hitbox_geo = new THREE.BoxGeometry(6, 1.1, 1.1);
        // 
        V3 dim = new V3(6.1, 1.1, 1.1);
        M4 mover = new M4().Identity().Move(-3.0, -0.6, -0.6);
        hitbox.boxHull = new BoxHull(dim);
        hitbox.boxHull.TransformObjectSpace(mover);
        velocity = .002f;
        rotation_speed = 0.01f;
        forward = true;
        back = false;
        left = false;
        right = false;
        moved = true;
        
    }
}
