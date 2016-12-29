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
console.log("Main init");
var playerManager = new tankette.PlayerManager("T34", scene);
var rocketManager = new tankette.ModelManager("rocket1opt", scene);
console.log("playerManger and rocketManager are done");
var turdleManager = new tankette.TurdleManager("rocket1opt", scene);
var dotManager = new tankette.DotManager("none", scene);
console.log("turdlemanager is done too!=) happy");

console.log("Player Manager: " + playerManager);
var ResetCamera = function() {
  camera.position.z = -26;
  camera.position.y = 14;
  camera.position.x = -26;
  camera.lookAt(zerovec);
};

var clock = new THREE.Clock();

//var helpie = new THREE.CameraHelper(dirlight.shadow);

var ambientLight = new THREE.AmbientLight(0x0c0c0c);
scene.add(ambientLight);

var spot1 = new THREE.SpotLight(0xffffff);
spot1.position.set(-200, 100, -10);
spot1.target.position.set(zerovec);
spot1.castShadow = true;
// TODO(sean): Which is correct?
if (true) {
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

var terrain = new tankette.Terrain();

// Rockets
//var rocket1 = new tankette.Rocket("rocket1opt", 20, 0.3, 20);
//scene.add(rocket1.group);
//test
var boxgeo = new THREE.BoxGeometry(2, 2, 2);
var redmat = new THREE.MeshLambertMaterial( {color: 0xff0000});
var cube = new THREE.Mesh(boxgeo, redmat);
cube.position.set(6, 0, -6);
cube.castShadow = true;
cube.receiveShadow = true;
var planegeo = new THREE.PlaneGeometry(40, 40, 10, 10);
var greenmat = new THREE.MeshLambertMaterial( {color: 0x22ff22});
var plane = new THREE.Mesh(planegeo, greenmat);
plane.rotation.x = -Math.PI * 0.5;
plane.position.set(0, -3.5, 0);
plane.castShadow = false;
plane.receiveShadow = true;

scene.add(spot1);
scene.add(terrain.group);
scene.add(cube);
scene.add(plane);

var controls = new WASD.Controls(undefined);
controls.movementSpeed = 5;
controls.lookSpeed = 0.05;

var chasecam = new tankette.ChaseCam(undefined, camera);
ResetCamera();
var use_chase_cam = false;
var can_change_cam = true;
var update_timeout = 0;

var skybox = new tankette.SkyBox(scene);
var textout = new tankette.Text();
textout.loadFont();



var pushBox = function() {
    // I have no idea what this does.
};

var Update = function() {
  var update_delta = clock.getDelta();
  update_timeout += update_delta;
  if (update_timeout > 30) {
      update_timeout = 0;
      console.log("Rockets: " + rocketManager.NumModels());
      // textout.erase();
      textout.configure("text", "Packet Length: " + packet_length);
      textout.generateGeometry();
      console.log("Players: " + playerManager.NumPlayers());
      console.log("Turdles: " + turdleManager.NumTurdles());
      pushBox();
  }
  //controls.update(update_delta);
  terrain.update();
  textout.update();
  pushButtons(controls);
};

var CamUpdate = function() {
  if (controls.toggleCam === true) {
    if (can_change_cam === true) {
      if (use_chase_cam === true) {
        use_chase_cam = false;
      } else {
        use_chase_cam = true;
      }
      can_change_cam = false;
    }
  } else {
    can_change_cam = true;
  }
  if (use_chase_cam === true) {
    chasecam.Update();
  } else {
    ResetCamera();
  }
  //rocket1.group.rotation.y  += 0.01;
};

var render = function() {
  requestAnimationFrame(render);
  renderer.render(scene, camera);
  Update();
};

render();
