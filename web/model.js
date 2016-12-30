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
  
  this.hitbox_geo = new THREE.BoxGeometry(6, 1.1, 1.1);
  this.hitball_geo = new THREE.SphereGeometry(3, 10, 7);
  
  var hitbox = new THREE.Mesh(this.hitbox_geo, tankette.PinkWire);
  var hitball = new THREE.Mesh(this.hitball_geo, tankette.RedWire);
  hitbox.position.x = -.0;
  this.group.add(hitbox);
  //this.group.add(hitball);
  
  var group = this.group;  // to be local and thus included in load() callback.
  
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
        object.position.x = 3.0;  // rocket specific.  Really need to instance..
        object.position.y = 0.0;
        object.position.z = 0.0;

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
  //var material = new THREE.Material();
  //material.clone(model.material);
  //var geometry = model.geometry.clone();
  //var mesh = new THREE.Mesh(geometry, material);
  // var loader = new THREE.OBJMTLLoader();
  // var group = this.group;
  //loader.load( "http://www.zettix.com/sean/js/3d/tank-model-2/tank-move/mdl/" + model + ".obj", "http://www.zettix.com/sean/js/3d/tank-model-2/tank-move/mdl/" + model + ".mtl",
  /* loader.load( "" + model + ".obj", "" + model + ".mtl",
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
     */
  //this.group.add(mesh);
  this.group.rotation.x = xr;
  this.group.rotation.y = yr;
  this.group.rotation.z = zr;
};