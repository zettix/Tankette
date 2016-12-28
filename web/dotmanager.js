/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/* global playerlist */
/* global THREE */
/* global controls */
/* global chasecam */


var tankette = tankette = tankette || {};

tankette.DotManager = function(model, scene) {
    this.players = [];
    this.model_ = model;
    this.self = this;
    this.scene = scene;
    
    this.ball_mat = new THREE.MeshLambertMaterial( {color: 0x554492});
    this.ball_geo = new THREE.SphereGeometry(0.1, 5, 7);
    
    this.AddDot = function(x, y, z) {
          var group = new THREE.Object3D();
          group.matrixAutoUpdate = true;
          group.position.x = x;
          group.position.y = y;
          group.position.z = z;
          var rockie = new THREE.Mesh(this.ball_geo, this.ball_mat);
          group.add(rockie);
        this.players.push(group);
         this.scene.add(group);
    };
    
    this.RemoveDots = function() {
      for (var i = 0; i < this.players.length; i++) {
           this.scene.remove(this.players[i]);
      }
      this.players = [];
    };
    
};