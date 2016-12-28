/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global THREE */

var tankette = tankette = tankette || {};

tankette.ChaseCam = function(target, chase_camera) {
  this.target = target;
  this.cam = chase_camera;
  this.offset_x = 14.0;
  this.offset_y = 7.0;
  this.offset_z = 0.0;
  this.look_x = 0.0;
  this.look_y = 0.0;
  this.look_z = 0.0;

  this.Update = function() {
    // Chase camera should have various constraints.
    // For now, attach to offset.
    var CamPos = new THREE.Vector3(this.offset_x, this.offset_y, this.offset_z);
    var AtPos = new THREE.Vector3(this.look_x, this.look_y, this.look_z);
    var Up = new THREE.Vector3(0.0, 1.0, 0.0);
    CamPos.applyAxisAngle(Up, this.target.group.rotation.y);
    AtPos.applyAxisAngle(Up,  this.target.group.rotation.y);
    CamPos.add(this.target.group.position);
    AtPos.add(this.target.group.position);
    this.cam.position.x = CamPos.x;
    this.cam.position.y = CamPos.y;
    this.cam.position.z = CamPos.z;
    //this.cam.position.set(CamPos);
    this.cam.lookAt(AtPos);
  };
};