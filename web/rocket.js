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
tankette.Rocket = function(model, x, y, z, xr, yr, zr) {
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
  this.hitbox_geo = new THREE.BoxGeometry(6, 1.1, 1.1);
  this.hitball_geo = new THREE.SphereGeometry(3, 10, 7);
  
  var hitbox = new THREE.Mesh(this.hitbox_geo, tankette.PinkWire);
  var hitball = new THREE.Mesh(this.hitball_geo, tankette.RedWire);
  hitbox.position.x = -.0;
  this.group.add(hitbox);
  this.group.add(hitball);
  this.HitMe = function() {
      hitball.material = tankette.YellowWire;
  };
  
  this.MissMe = function() {
      hitball.material = tankette.RedWire;
  };

  var loader = new THREE.OBJMTLLoader();
  var group = this.group;
  console.log("LOADER " + model + " AAAAAAAAAAAAAAAAAAAAA");
  loader.load( "" + model + ".obj", "" + model + ".mtl",
      function(object) {
        object.castShadow = true;
        object.receiveShadow = true;
        // Object.positon = relative to group.
        object.position.x = 3.0;
        object.position.y = 0.0;
        object.position.z = 0.0;
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