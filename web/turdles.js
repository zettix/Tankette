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
  
  var group = this.group;
  var loaded = false;
  var js_loaded = false;

  var mloader = new THREE.MTLLoader();
  var onProgress = function ( xhr ) {
   if ( xhr.lengthComputable ) {
      var percentComplete = xhr.loaded / xhr.total * 100;
      console.log( Math.round(percentComplete, 2) + '% downloaded' );
      }
  };
  var onError = function ( xhr ) { };
  mloader.setPath('mdl/');
  mloader.load(model + ".mtl",
    function(materials) {
        materials.preload();
        var objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.setPath("mdl/");
        objLoader.load(model + ".obj", function(object) {
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
                js_loaded = true;
                loaded = true;
          }, onProgress, onError);
        }
      );
  this.group.rotation.x = xr;
  this.group.rotation.y = yr;
  this.group.rotation.z = zr;
};
