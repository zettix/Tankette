/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sean
 */
public class ModelManager <T extends ModelInterface & Object3dInterface> {
    private final HashMap<String, T> models;
    private final HitboxHandler HITBOXHANDER;
    
    private long serial = 0l;
    
    private final DecimalFormat NF = new DecimalFormat("#####");

    public ModelManager(HitboxHandler h) {
        HITBOXHANDER = h;
        this.models = new HashMap<>();
    }
    
    public synchronized String GetNewSerial() {
        return NF.format(serial++);
    }
        
    public synchronized T getModelById(String id) {
        if (models.containsKey(id)) {
            return models.get(id);
        }
        return null;
    }

    public synchronized void addModel(String id, T p) {
        if (models.containsKey(id)) {
            // TODO complain.
        } else {
            models.put(id, p);
        }
    }

    public synchronized void delModel(String id) {
        if (models.containsKey(id)) {
            models.remove(id);
            HITBOXHANDER.DelModel(id);
        } else {
            // TODO: complain.
        } 
    }

    public synchronized void moveModel(String id) {
        if (models.containsKey(id)) {
            // TODO: Game logic.

        } else {
            // TODO: complain.
        } 
    }
    
    public synchronized void updateModels(long now) {
        for(T t : models.values()) {
            t.Update(now);
            if (t.isDone()) {
                String id = t.getId();
                delModel(id);
            }
        }
    }

    public List<String> getModelIdsAsList() {
       return new ArrayList<>(models.keySet());
    }
}
