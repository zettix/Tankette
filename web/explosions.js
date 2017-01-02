/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global THREE */


var tankette = tankette = tankette || {};
tankette.RedWire = new THREE.MeshBasicMaterial( {color: 0xff0000,
                                             wireframe: true});
tankette.YellowWire = new THREE.MeshBasicMaterial( {color: 0xffff00,
                                             wireframe: true});
tankette.PinkWire = new THREE.MeshBasicMaterial( {color: 0xff8000,
                                             wireframe: true});
tankette.OrangeWire = new THREE.MeshBasicMaterial( {color: 0x888800,
                                             wireframe: true});
tankette.Explosion = function(model_name, x, y, z, xr, yr, zr, s) {
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
  var group = this.group;  // to be local and thus included in load() callback.

    
  // Hitboxes:
  this.hitbox_geo = new THREE.BoxGeometry(6, 5.1, 5.1);
  this.hitball_geo = new THREE.SphereGeometry(5, 10, 7);  
  var hitbox = new THREE.Mesh(this.hitbox_geo, tankette.PinkWire);
  var hitball = new THREE.Mesh(this.hitball_geo, tankette.RedWire);
  hitbox.position.x = -.0;
  this.group.add(hitbox);
  this.group.add(hitball);
  
  var mloader = new THREE.MTLLoader();
  console.log("Hello, I'm an explosion!  Boom! " + model_name);
  var onProgress = function ( xhr ) {
   if ( xhr.lengthComputable ) {
      var percentComplete = xhr.loaded / xhr.total * 100;
      console.log( Math.round(percentComplete, 2) + '% downloaded' );
      }
  };
  var onError = function ( xhr ) { };
  mloader.setPath('mdl/');
  mloader.load(model_name + ".mtl",
    function(materials) {
        materials.preload();
        var objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.setPath("mdl/");
        objLoader.load(model_name + ".obj", function(object) {
        // Object.positon = relative to group.
        // TODO(sean): disable lighting.
        object.position.x = 0.0;  
        object.position.y = 0.0;
        object.position.z = 0.0;
        // Object.positon = relative to group.
        object.scale.x = 10.0;  // animate explosion with scale and rotate.
        object.scale.y = 10.0;
        object.scale.z = 10.0;
        //object.rotation.y = -Math.PI * 0.5;
        // object.rotation.x = Math.PI * 0.5;
        // object.rotation.z = Math.PI * 0.5;
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
     
  this.MoveForward = function(delta) {
    var cosy = Math.cos(this.group.rotation.y);
    var siny = Math.sin(this.group.rotation.y);
    this.group.position.x -= delta * cosy * this.velocity;
    this.group.position.z += delta * siny * this.velocity;
    pushBox(this);
  };
  this.MoveBackward = function(delta) {
    var cosy = Math.cos(this.group.rotation.y);
    var siny = Math.sin(this.group.rotation.y);
    this.group.position.x += delta * cosy * this.velocity;
    this.group.position.z -= delta * siny * this.velocity;
    pushBox(this);
  };
  this.MoveLeft = function(delta) {
    this.group.rotation.y += delta * this.rotation_speed;
    pushBox(this);
  };

  this.MoveRight = function(delta) {
    this.group.rotation.y -= delta * this.rotation_speed;
    pushBox(this);
  };
  // TODO: (sean) hitboxes and explosions, thrust.
  return this;
};


