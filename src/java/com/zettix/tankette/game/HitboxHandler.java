/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.graphics.gjkj.util.M4;
import com.zettix.graphics.gjkj.util.V3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sean
 */
public class HitboxHandler {
    
    private final HashMap<String, Hitbox> modelHitboxes = new HashMap<>();
    public Set<Dot> dots = new HashSet<>();
    
    public void AddModel(Model p) {
        String id = p.getId();
        if (modelHitboxes.containsKey(id)) {
            // pass, log error if possible.
        } else {
            modelHitboxes.put(p.getId(), p.hitbox);
        }
    }
    
    public String GetHits() {
        StringBuilder out = new StringBuilder();
        Set keyset = modelHitboxes.keySet();
        List<String> keylist = new ArrayList(keyset);
        int listsize = keylist.size();
        
        out.append("HitboxHandler: ");
        // for now clear since lists set up.
        for (int i = 0; i < listsize; i++) {
            Hitbox h = (Hitbox) modelHitboxes.get(keylist.get(i));
            out.append(i);
            out.append(" ");
            if (h.is_hit) {
                out.append("H");
            } else {
                out.append("-");                
            }
        }
        return out.toString();
    }

    public void DelModel(String id) {
        if (modelHitboxes.containsKey(id)) {
            modelHitboxes.remove(id);
        } else {
            // pass, log error if possible.
        }
    }
    
    public boolean IsHit(Model p) {
        String id = p.getId();
        if (modelHitboxes.containsKey(id)) {
            Hitbox h = (Hitbox) modelHitboxes.get(p.getId());
            return h.is_hit;
        } else {
            // pass, log error if possible.
        }
        return false;
    }
    
    public String DetectCollisions() {
        // basically compare all hitboxes to each other.  Good ole n squared.
        // meaning this can get out of hand fast.  easiest is to have two
        // indexes into the map, but I have to put the keys into a list
        // first then go 1..n+2..n,
        Set<String> keyset = modelHitboxes.keySet();
        List<String> keylist = new ArrayList(keyset);
        int listsize = keylist.size();
        
        StringBuilder out = new StringBuilder();
        
        out.append("Detect: ");
        dots.clear();
        // for now clear since lists set up.
        for (int i = 0; i < listsize; i++) {
            Hitbox h = (Hitbox) modelHitboxes.get(keylist.get(i));
            Model p = h.model;
            M4 modelTransform = new M4().Identity().Move(p.getX(), p.getY(), p.getZ()).Rotate(0.0, -p.getYr(), 0.0);
            
            h.boxHull.TransformWorldSpace(modelTransform);
            for (int xx = 0; xx < 8; xx++) {
                V3 v = h.boxHull.GetCorner(xx);
                Dot d = new Dot();
                d.setX(v.get(0));
                d.setY(v.get(1));
                d.setZ(v.get(2));
                dots.add(d);
            }
                    
            // h.boxHull.TransformWorldSpace(modelTransform);
            h.is_hit = false;
            h.ClearHits();
        }
        
        for (int i = 0; i < listsize - 1; i++) {
            try {
                Hitbox h1 = (Hitbox) modelHitboxes.get(keylist.get(i));
                for (int j = i + 1; j < listsize; j++) {
                    Hitbox h2 = (Hitbox) modelHitboxes.get(keylist.get(j));
                    Model p1 = h1.model;
                    Model p2 = h2.model;
                    out.append("[ ");
                    out.append(i);
                    out.append(" p1: ");
                    out.append(p1.getId());
                    out.append(" ~");
                    out.append(j);
                    out.append(" p2: ");
                    out.append(p2.getId());

                    // if (0.0f < h.TestHitSphere(p)) {
                    if (!h1.TestHit(p2)) {
                        //pass
                        out.append(" Miss] ");
                    } else {
                        // do stuff, maybe add who hit whom later.
                        h2.AddHit(p1);
                        h1.AddHit(p2);
                        h1.is_hit = true;
                        h2.is_hit = true;
                        out.append(" Hit!]");
                    }
                    /*p = h.model;
                    if (h2.TestHitSphere(p)) {
                        // do stuff, maybe add who hit whom later.
                        h.is_hit = true;
                        h2.is_hit = true;
                    } */
                }
            } catch (java.lang.NullPointerException ex) {
                // pass.  this is simultanious access, somehow this thread
                // was running while another was.  The timer?
            }
        }
        return out.toString();
    }
}