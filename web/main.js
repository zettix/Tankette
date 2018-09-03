/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global THREE */
/* global WASD */
/* global tankette */

/* scene and stuff */
var scene = new THREE.Scene();
var aspect = window.innerWidth / window.innerHeight;
var camera = new THREE.PerspectiveCamera(75, aspect, 0.1, 1000);
var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);
var packet_length = 0;  // pre format: 480 for 3 players.

var zerovec = new THREE.Vector3(0, 0, 0);
renderer.shadowMap.enabled = true;
renderer.shadowMapSoft = true;
renderer.shadowMap.type = THREE.PCFSoftShadowMap; // default THREE.PCFShadowMap

console.log("Main init");
var playerManager =     new tankette.PlayerManager("T34", scene, "t1");
var explosionManager =  new tankette.ModelManager(
                            "ballexplosion", tankette.Explosion, scene);
var rocketManager =     new tankette.ModelManager(
                            "rocket1opt", tankette.Rocket, scene);
//var bikeManager =       new tankette.ModelManager(
//                            "rocket1opt", tankette.Rocket, scene);
var bikeManager = new tankette.ModelManager(
                          "alien_on_a_bike", tankette.Bike, scene);
var turdleManager = new tankette.TurdleManager("rocket1opt", scene);
var dotManager = new tankette.DotManager("none", scene);
var terrainManager = new tankette.TerrainManager(scene);

console.log("Player Manager: " + playerManager);
console.log("Rocket manager:" + rocketManager);
console.log("Explosion manager:" + explosionManager);
console.log("Turdle manager:" + turdleManager);
console.log("Terrain Manager: " + terrainManager);
console.log("Bike Manager: " + bikeManager);

var clock = new THREE.Clock();

//var helpie = new THREE.CameraHelper(dirlight.shadow);

var ambientLight = new THREE.AmbientLight(0x0c0c0c);
scene.add(ambientLight);

var spot1 = new THREE.SpotLight(0xffffff);
spot1.position.set(-200, 100, -10);
spot1.target.position.set(zerovec);
spot1.castShadow = true;
// TODO(sean): Which is correct?
if (false) {
  spot1.shadowMapWidth = 1024;
  spot1.shadowMapHeight = 1024;
  spot1.shadowCameraNear = 50;
  spot1.shadowCameraFar = 4000;
  spot1.shadowCameraFov = 30;
  spot1.intensity = 2.0;
} else {
  spot1.shadow.mapSize.width = 1024;
  spot1.shadow.mapSize.height = 1024;
  spot1.shadow.camera.near = 50;
  spot1.shadow.camera.far = 4000;
  spot1.shadow.camera.fov = 30;
}

var sunlight =  new THREE.DirectionalLight( 0xffffff, 1, 100 );
sunlight.position.set( 1, 1, 1 ); 			//default; light shining from top
sunlight.castShadow = true;            // default false
scene.add( sunlight );
//Set up shadow properties for the light
sunlight.shadow.mapSize.width = 512;  // default
sunlight.shadow.mapSize.height = 512; // default
sunlight.shadow.camera.near = 0.5;    // default
sunlight.shadow.camera.far = 500;     // default

var helper = new THREE.CameraHelper(sunlight.shadow.camera );
scene.add( helper );

var terrain = new tankette.Terrain();

var boxgeo = new THREE.BoxGeometry(2, 2, 2);
var redmat = new THREE.MeshLambertMaterial( {color: 0xff0000});
var cube = new THREE.Mesh(boxgeo, redmat);
cube.position.set(1, 1 , 1);
cube.castShadow = true;
cube.receiveShadow = true;
//var planegeo = new THREE.PlaneGeometry(40, 40, 10, 10);
var greenmat = new THREE.MeshLambertMaterial( {color: 0x22ff22});

//scene.add(spot1);
//scene.add(terrain.group);
scene.add(cube);

var controls = new WASD.Controls(undefined);
controls.movementSpeed = 5;
controls.lookSpeed = 0.05;

var chasecam = new tankette.ChaseCam(undefined, controls, camera);
chasecam.ResetCamera();
playerManager.SetCam(chasecam);

var update_timeout = 0;
var can_wireframe = false;
var use_wireframe = false;

var skybox = new tankette.SkyBox(scene);
var textout = new tankette.Text();
textout.loadFont();

var websocket_packet_txt = "";

var pushBox = function() {
    // I have no idea what this does.
};

var Update = function() {
  var update_delta = clock.getDelta();
  update_timeout += update_delta;
  if (update_timeout > 5) {
      update_timeout = 0;
      console.log("Rockets: " + rocketManager.NumModels());
      // textout.erase();
      textout.configure("text", "Packet Length: " + packet_length);
      textout.generateGeometry();
      console.log("Players: " + playerManager.NumPlayers());
      console.log("Turdles: " + turdleManager.NumTurdles());
      console.log("Explosions:" + explosionManager.NumModels());
      console.log("Explosions " + explosionManager.Print());
      console.log("Bikes:" + bikeManager.NumModels());
      console.log("Packet: " + websocket_packet_txt);
      console.log("Terrain" + terrainManager.Summary());
      console.log("Wireframe: " + use_wireframe);
      pushBox();
  }
  //controls.update(update_delta);
  WireFrameUpdate();
  terrain.update();
  textout.update();
  //chasecam.CamUpdate();
  pushButtons(controls);
};

// all this jazz is to make a T flip flop.
var WireFrameUpdate = function() {
    if (controls.toggleWireFrame === true) {
        if (can_wireframe === true) {
            can_wireframe = false;
            if (use_wireframe === true) {
                // disable wireframe;
                use_wireframe = false;
            } else {
                // enable wireframe
                use_wireframe = true;
            }
        }
    } else {
        can_wireframe = true;
    }
};

var render = function() {
  requestAnimationFrame(render);
  renderer.render(scene, camera);
  Update();
};

render();
