/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game.interfaces;

import com.zettix.tankette.game.Model;

/**
 *
 * @author sean
 */
public interface ModelInterface {
   public boolean isDone();
   public void setDone();
   public void Update(long now, double delta);
   public Model.Collider getCollider();
}
