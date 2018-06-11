/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global THREE */
/* global zerovec */

var tankette = tankette = tankette || {};

tankette.ChaseCam = function(target, controls, chase_camera) {
  this.controls = controls;
  this.target = target;
  this.cam = chase_camera;
  this.offset_x = 14.0;
  this.offset_y = 7.0;
  this.offset_z = 0.0;
  this.look_x = 0.0;
  this.look_y = 0.0;
  this.look_z = 0.0;
  this.inc = 0.3;
  this.can_change_cam = true;
  this.use_chase_cam = true;

  this.ResetCamera = function() {
    this.cam.position.z = -26;
    this.cam.position.y = 14;
    this.cam.position.x = -26;
    this.cam.lookAt(zerovec);
  };

  this.CamUpdate = function() {
    if (controls.toggleCam === true) {
      if (this.can_change_cam === true) {
        if (this.use_chase_cam === true) {
          this.use_chase_cam = false;
        } else {
          this.use_chase_cam = true;
        }
        this.can_change_cam = false;
      }
    } else {
      this.can_change_cam = true;
    }
    if (this.use_chase_cam === true) {
      this.Update();
    } else {
      ResetCamera();
    }
  };

  this.Update = function() {
    // Chase camera should have various constraints.
    // For now, attach to offset.
    if (controls.camUp) {
        this.offset_y += this.inc;
    }
    if (controls.camLeft) {
        this.offset_z += this.inc;
        this.look_z += this.inc;
    }
    if (controls.camRight) {
        this.offset_z -= this.inc;
        this.look_z -= this.inc;
    }
    if (controls.camDown) {
        this.offset_y -= this.inc;
    }
    if (controls.camForward) {
        this.offset_x -= this.inc;
        this.look_x -= this.inc;
    }
    if (controls.camBackward) {
        this.offset_x += this.inc;
        this.look_x += this.inc;
    }
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
