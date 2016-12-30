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

tankette.Tank = function(model, x, y, z, xr, yr, zr) {
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
  
  this.hitbox_geo = new THREE.BoxGeometry(8, 4, 4.5);
  this.hitball_geo = new THREE.SphereGeometry(7, 10, 7);

  var hitbox = new THREE.Mesh(this.hitbox_geo, tankette.PinkWire);
  var hitball = new THREE.Mesh(this.hitball_geo, tankette.RedWire);
  
  hitbox.position.y = 2;
   this.group.add(hitbox);
  
  //this.group.add(hitball);
  this.HitMe = function() {
      hitball.material = tankette.YellowWire;
  };

  this.MissMe = function() {
      hitball.material = tankette.RedWire;
  };


  this.turret_rotation = Math.random() * 0.2;
  this.barrel_rotation = Math.random() * 0.2;

  this.turret_rotator = new THREE.Object3D();
  this.barrel_rotator = new THREE.Object3D();
  this.barrel_rotator.position.y = 3.0; 
  this.barrel_rotator.position.x = -2.0; 

  this.group.add(this.turret_rotator);
  this.turret_rotator.add(this.barrel_rotator);

  var group = this.group;
  var turret_rotator = this.turret_rotator;
  var barrel_rotator = this.barrel_rotator;

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
  var superloader = function(model_in, pawpaw, xin, yin) {
    mloader.load(model_in + ".mtl",
      function(materials) {
          materials.preload();
          var objLoader = new THREE.OBJLoader();
          objLoader.setMaterials(materials);
          objLoader.setPath("mdl/");
          objLoader.load(model_in + ".obj", function(object) {
               object.castShadow = true;
                object.receiveShadow = true;
                object.traverse( function( node ) { if ( node instanceof THREE.Mesh ) { node.receiveShadow = true; node.castShadow = true; } } );
                object.position.y = yin;
                object.position.x = xin;
                js_loaded = true;
                loaded = true;
                pawpaw.add(object);
                }, onProgress, onError);
        }
      );
   };   // superloader

  superloader("T34body", group, 0.0, 0.0);
  superloader("T34turret", turret_rotator, 0.0, 0.0);
  superloader("T34barrel", barrel_rotator, 2.0, -3.0);

  this.AimTurret = function(deg) {
    turret_rotator.rotation.y = deg;
  };

  this.AimBarrel = function(deg) {
    barrel_rotator.rotation.z = deg;
  };


  this.MoveForward = function(delta) {
    var cosy = Math.cos(this.group.rotation.y);
    var siny = Math.sin(this.group.rotation.y);
    this.group.position.x -= delta * cosy * this.velocity;
    this.group.position.z += delta * siny * this.velocity;
  };

  this.MoveBackward = function(delta) {
    var cosy = Math.cos(this.group.rotation.y);
    var siny = Math.sin(this.group.rotation.y);
    this.group.position.x += delta * cosy * this.velocity;
    this.group.position.z -= delta * siny * this.velocity;
  };
  this.MoveLeft = function(delta) {
    this.group.rotation.y += delta * this.rotation_speed;
  };

  this.MoveRight = function(delta) {
    this.group.rotation.y -= delta * this.rotation_speed;
  };
  return this;
};
