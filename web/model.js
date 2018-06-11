/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global THREE */

var tankette = tankette = tankette || {};

tankette.Model = function(model, x, y, z, xr, yr, zr, s) {
  this.x = x;
  this.y = y;
  this.z = z;
  return model(x, y, z, xr, yr, zr, s);
};