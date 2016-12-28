/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.players;

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
    
    private final HashMap playerHitboxes = new HashMap<>();
    public Set<Dot> dots = new HashSet<>();
    
    public void AddPlayer(Player p) {
        String id = p.getId();
        if (playerHitboxes.containsKey(id)) {
            // pass, log error if possible.
        } else {
            playerHitboxes.put(p.getId(), p.hitbox);
        }
    }
    
    public String GetHits() {
        StringBuilder out = new StringBuilder();
        Set keyset = playerHitboxes.keySet();
        List keylist = new ArrayList(keyset);
        int listsize = keylist.size();
        
        out.append("HitboxHandler: ");
        // for now clear since lists set up.
        for (int i = 0; i < listsize; i++) {
            Hitbox h = (Hitbox) playerHitboxes.get(keylist.get(i));
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

    public void DelPlayer(String id) {
        if (playerHitboxes.containsKey(id)) {
            playerHitboxes.remove(id);
        } else {
            // pass, log error if possible.
        }
    }
    
    public boolean IsHit(Player p) {
        String id = p.getId();
        if (playerHitboxes.containsKey(id)) {
            Hitbox h = (Hitbox) playerHitboxes.get(p.getId());
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
        Set keyset = playerHitboxes.keySet();
        List keylist = new ArrayList(keyset);
        int listsize = keylist.size();
        
        StringBuilder out = new StringBuilder();
        
        out.append("Detect: ");
        dots.clear();
        // for now clear since lists set up.
        for (int i = 0; i < listsize; i++) {
            Hitbox h = (Hitbox) playerHitboxes.get(keylist.get(i));
            Player p = h.player;
            M4 playerTransform = new M4().Identity().Move(p.getX(), p.getY(), p.getZ()).Rotate(0.0, -p.getYr(), 0.0);
            
            h.boxHull.TransformWorldSpace(playerTransform);
            for (int xx = 0; xx < 8; xx++) {
                V3 v = h.boxHull.GetCorner(xx);
                Dot d = new Dot();
                d.setX(v.get(0));
                d.setY(v.get(1));
                d.setZ(v.get(2));
                dots.add(d);
            }
                    
            // h.boxHull.TransformWorldSpace(playerTransform);
            h.is_hit = false;
        }
        
        for (int i = 0; i < listsize - 1; i++) {
            Hitbox h = (Hitbox) playerHitboxes.get(keylist.get(i));
            for (int j = i + 1; j < listsize; j++) {
                Hitbox h2 = (Hitbox) playerHitboxes.get(keylist.get(j));
                Player p = h2.player;
                Player pp = h.player;
                out.append("[ ");
                out.append(i);
                out.append(" p1: ");
                out.append(p.getId());
                out.append(" ~");
                out.append(j);
                out.append(" p2: ");
                out.append(pp.getId());

                // if (0.0f < h.TestHitSphere(p)) {
                if (!h.TestHit(p)) {
                    //pass
                    out.append(" Miss] ");
                } else {
                    // do stuff, maybe add who hit whom later.
                    h.is_hit = true;
                    h2.is_hit = true;
                    out.append(" Hit!]");
                }
                 /*p = h.player;
                 if (h2.TestHitSphere(p)) {
                    // do stuff, maybe add who hit whom later.
                    h.is_hit = true;
                    h2.is_hit = true;
                } */
            }
        }
        return out.toString();
    }
}