/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global THREE */

var tankette = tankette = tankette || {};

tankette.Turdle = function(model, x, y, z, xr, yr, zr, s) {
  this.x = x;
  this.y = y;
  this.z = z;

  this.velocity = 19.1;
  this.rotation_speed = 1.1;
  this.group = new THREE.Object3D();
  this.group.matrixAutoUpdate = true;
  this.group.position.x = x;
  this.group.position.y = y;
  this.group.position.z = z;
  this.group.scale.x = s;
  this.group.scale.y = s;
  this.group.scale.z = s;
  
  var loader = new THREE.OBJMTLLoader();
  var group = this.group;
  //loader.load( "http://www.zettix.com/sean/js/3d/tank-model-2/tank-move/mdl/" + model + ".obj", "http://www.zettix.com/sean/js/3d/tank-model-2/tank-move/mdl/" + model + ".mtl",
  loader.load( "" + model + ".obj", "" + model + ".mtl",
      function(object) {
        object.castShadow = true;
        object.receiveShadow = true;
        // Object.positon = relative to group.
        object.scale.x = 2.0;
        object.scale.y = 2.0;
        object.scale.z = 2.0;
        //object.rotation.y = -Math.PI * 0.5;
        object.rotation.x = Math.PI * 0.5;
        object.rotation.z = Math.PI * 0.5;
        object.traverse( function( node ) { if ( node instanceof THREE.Mesh ) { node.receiveShadow = true; node.castShadow = true; } } );
        group.add(object);
     });
     
  this.group.rotation.x = xr;
  this.group.rotation.y = yr;
  this.group.rotation.z = zr;

};