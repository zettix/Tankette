# Tankette

## Status: Incomplete

Tankette is a web based networked multiplayer tank game.  A variety of
technologies are used:

1. ThreeJS
2. WebSocket
3. My GJK solver
4. Java servelets with servlet engine
5. javascript for client

As of 12/30/2016
Deploying the war file creates the necessary sessions, however the IP/port
is hard coded in websocket.js

Once deployed, arrow keys, wasd move.  "c" changes the camera from fixed to
a chase camera.  "f" or spacebar fires.  "t" makes "turdles" which are
rotating/scaling copies of rockets for eye candy and testing.

The following screenshot shows what a game session currently looks like.

![Tankette](https://github.com/zettix/Tankette/blob/master/resources/Tankette.png)

Older video of "turdles" and hitbox integration:
https://www.youtube.com/watch?v=KdKjAcz8mmg

### Author: Sean Brennan, 2016
