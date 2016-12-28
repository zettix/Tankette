/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global THREE */

var tankette = tankette = tankette || {};

tankette.Terrain = function() {
  this.x = 0;
  this.y = 0;
  this.z = 0;
  var xdim = 19;
  var zdim = 19;
  var dx = 45.3;
  var dz = 45.3;

  // Terrain is what?  at 3x3? 4x4? 5x5?
  // and how about players fighting over more than that?
  
  this.group = new THREE.Object3D();
  this.group.position.x = this.x;
  this.group.position.y = this.y;
  this.group.position.z = this.z;
  var texture = {};
  var bumpmap = {};
  var main_texture_loaded = false;
  var bump_texture_loaded = false;
  var loaded = false;
  var bumpscale = 1.0;


  this.tiles = [];
  this.MakeMesh = function (ground_material) {
    var xc = xdim * dx * 0.5;
    var zc = zdim * dz * 0.5;
    for (var ix = 0; ix < xdim; ix += 1) {
      for (var iz = 0; iz < zdim; iz += 1) { 
        var ground_geometry = new THREE.PlaneGeometry(dx, dz);
        var ground_mesh = new THREE.Mesh(ground_geometry, ground_material);
        ground_mesh.position.x = ix * dx - xc;
        ground_mesh.position.z = iz * dz - zc;
        ground_mesh.position.y = -0;
        ground_mesh.rotation.x = - Math.PI * 0.5;
        ground_mesh.receiveShadow = true;
        tiles.push(ground_mesh);
        group.add(ground_mesh);
      };
    };
  };

  // instantiate a loader
  var loader = new THREE.TextureLoader();
  var MakeMesh = this.MakeMesh;
  var tiles = this.tiles;
  var group = this.group;
 
  // load a resource
  loader.load(
	// resource URL
  'mdl/ground.png',
	// Function when resource is loaded
	function ( tex ) {
		// do something with the texture
	  //	var material = new THREE.MeshBasicMaterial( {
	  //		map: texture
	  // } );
           texture = tex;
           main_texture_loaded = true;
           console.log('main tex loaded');
	},
	// Function called when download progresses
	function ( xhr ) {
		console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );
	},
	// Function called when download errors
	function ( xhr ) {
		console.log( 'An error happened' );
	}
  );

  loader.load(
  // resource URL
  //'ground_bump.png',
  'mdl/bumps/2896-bump.jpg',
  //'mdl/bumps/snake.col.jpg',
  //'mdl/bumps/normal.jpg',
	// Function when resource is loaded
	function ( tex ) {
		// do something with the texture
	  //	var material = new THREE.MeshBasicMaterial( {
	  //		map: texture
	  // } );
            bumpmap = tex;
            bump_texture_loaded = true;
            console.log('bump loaded');
	},
	// Function called when download progresses
	function ( xhr ) {
		console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );
	},
	// Function called when download errors
	function ( xhr ) {
		console.log( 'An error happened' );
	}
  );

  this.update = function() {
    if (loaded === false) {
      if ((main_texture_loaded === true) &&
          (bump_texture_loaded === true)) {
        var material = new THREE.MeshPhongMaterial({ map: texture,
                                                     bumpMap: bumpmap,
                                                     bumpScape: bumpscale});
        MakeMesh(material);
        loaded = true;
        console.log('terrain_loaded');
      }
    }
  };
  return this;
};

