// "Face Painting"
// Projection/installation project
// Current version - 8:10 IC

// Requires openCV, minim, fisica 
// All available as libraries in Processing.


// READ-ME
// Volume changes color of the fisica balls
// <space> changes color of the face tracking
// face tracking from webcam is flipped to be more intuitive for projection
// Fisica balls will eventually turn one color with no activity
// Face tracking rectangles will disappear with no activity
// (Sound changes opacity of face tracking, so visible with activity)

// Wow! Fun! Let's go face painting!

import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencv;

// canvas
int width = 1520;
int height = 440;
// drawing
int num = 200;
int[] drawingArrayX = new int[num];
int[] drawingArrayY = new int[num];
int[] faceWidthX = new int[num];
int[] faceHeightY = new int[num];
int faceX = -1;
int faceY = -1;
int faceWidthXVar = -1;
int faceHeightYVar = -1;
// colors
int[] rArray = new int[num];
int[] gArray = new int [num];
int[] bArray = new int [num];
int R = 137;
int G = 255;
int B = 255;
int opacity = 50;
// audio things
int onTime;


// fisica initialization

import fisica.*;

FWorld world;
































void setup() {

  video = new Capture(this, 640/2, 480/2);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);

  size(width, height);
  smooth ();
  background(#555555);

  video.start();

  // fisica setup

  Fisica.init(this);

  world = new FWorld();
  world.setGravity(0, 100);
  world.setEdges();


  // audio

  setupAudio();
  onTime = 0;
}
























void draw() {

  ////// fisica contact resize ////


  background(#000000);

  if (frameCount % 25 == 0) {
    float sz = random(30, 60);
    FCircle b = new FCircle(sz);
    b.setPosition(random(0+30, width-30), 50);
    b.setVelocity(0, 200);
    b.setRestitution(0.7);
    b.setDamping(0.01);
    b.setNoStroke();
    b.setFill(R,G,B,150);
    world.add(b);
  }

  world.draw();
  world.step();




  /////// MINIM AUDIO STUFF ////////


  getVolume();
  // float Y = map(volume,0,100,2,120);
  println(volume);
  if (volume > 100) {
    onTime = millis();
    println("AAAAAAAAAAAAA");
    R = int(random(0, 255));
    G = int(random(0, 255));
    B = int(random(0, 255));
  }

  if (millis() - onTime < 1000) {
    R = int(random(0, 255));
    G = int(random(0, 255));
    B = int(random(0, 255));
  } 



  //////// OPEN CV VIDEO STUFF ////////

  opencv.loadImage(video);




  ////////// drawing w face /////////

  noFill();
  stroke(#ffffff);
  //  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  println(faces.length);


 noStroke();
  // fill(R, G, B, 75);

  for (int i = 0; i < faces.length; i++) {
    println(faces[i].x + "," + faces[i].y);
    faceX = width - faces[i].x * 7;
    faceY = faces[i].y * 7;
    faceWidthXVar = faces[i].width;
    faceHeightYVar = faces[i].height;
  }


  // SHIFT OVER INDEX
  for (int i = num - 1; i > 0; i--) {
    drawingArrayX[i] = drawingArrayX[i-1];
    drawingArrayY[i] = drawingArrayY[i-1];
    faceWidthX[i] = faceWidthX[i-1];
    faceHeightY[i] = faceHeightY[i-1];
    rArray[i] = rArray[i-1];
    gArray[i] = gArray[i-1];
    bArray[i] = bArray[i-1];
  }

  // add new values
  drawingArrayX[0] = faceX;
  drawingArrayY[0] = faceY;
  faceWidthX[0] = faceWidthXVar;
  faceHeightY[0] = faceHeightYVar;

  if (keyPressed){
      rArray[0] = int(random(0, 255));
      gArray[0] = int(random(0, 255));
      bArray[0] = int(random(0, 255));
  } else {
    rArray[0] = rArray[1];
    gArray[0] = gArray[1];
    bArray[0] = bArray[1];
  }
  

  // draw everything!!!
  for (int i = num-1; i >= 0; i--) {
    if (drawingArrayX[i] != 0) {
      opacity = int(map(volume,0,150,10,150));
      fill(rArray[i],gArray[i],bArray[i],volume);
      rect(drawingArrayX[i], drawingArrayY[i], faceWidthX[i], faceHeightY[i]);
    }
  }



    
  // keyboard functions
  if (key == 'e') {
    R = 255;
    G = 255;
    B = 255;
  }



  /////// fence blocking /////// 

  noStroke();
  fill(#000000);
  rect(220, 0, 1080, 80);
  rect(220, 200, 1080, 50);
  rect(220, 390, 1080, 50); 


  pushMatrix();
  scale(.3);
  image(video, width+650, 0 );
  popMatrix();

  fill(0,0,0,50);
  rect(220, 0, 1080, 80);

  //////// display current settings //////////

  fill(R, G, B);
  ellipse(490, 25, 15, 15);
  fill(rArray[0], gArray[0], bArray[0]);
  ellipse(900, 25, 15, 15);
  fill(#ffffff);
  text("volume changes ball color", 300, 25);
  text("<space> to change square color.", 950, 25);
}




void captureEvent(Capture c) {
  c.read();
}




void contactEnded(FContact c) {  
  if (!c.getBody1().isStatic()) {
    FCircle b = (FCircle)c.getBody1();
    if (b.getSize()>5) {
      b.setSize(b.getSize()*0.9);
    }
  } 

  if (!c.getBody2().isStatic()) {
    FCircle b = (FCircle)c.getBody2();
    if (b.getSize()>5) {
      b.setSize(b.getSize()*0.9);
    }
  }
}


