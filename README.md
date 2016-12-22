AnalogStickLibrary
==================

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AnalogStickLibrary-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1049)
[![](https://jitpack.io/v/DeveloperPaul123/AnalogStickLibrary.svg)](https://jitpack.io/#DeveloperPaul123/AnalogStickLibrary)

A simple library that provides an anlog stick to use for on screen controls.


This library is an easy way to implement an analog stick for on screen controls in your app!. 

<h2> Dependency </h2>
This library is available on Maven so simply add the following line in your build.gradle file. 

````java
repositories {
  maven {url "https://jitpack.io"}
}

dependencies {
  compile 'com.github.DeveloperPaul123:AnalogStickLibrary:1.0.0'
}
  ````
<h2> Usage </h2>

Using the library is easy. Just add the analog stick to your layout via xml. You can set the inner and outer circle colors too!
Then in your activity reference it the same way you would any other view:

````java

private AnalogStick analogStick;

....

protected void OnCreate(Bundle bundle) {

  setContentView(R.layout.mylayout);
  
  analogStick = (AnalogStick) findViewById(R.id.my_analog);
  
  }
  ````
Be sure to also set a listener so that you can retrieve the returned values. 
If you want a "power" reading or some scaled value do the following as well. 

````java
        //set max x and y values. 
        analogStick.setMaxYValue(30f);
        analogStick.setMaxXValue(30f);
        //add the listner. 
        analogStick.setOnAnalogMoveListner(new OnAnalogMoveListener() {
            @Override
            public void onAnalogMove(float x, float y) {
               //do something with the raw values. 
            }

            @Override
            public void onAnalogMovedScaledX(float scaledX) {
               //do something with the scaled x value. 
            }

            @Override
            public void onAnalogMovedScaledY(float scaledY) {
               //do something with the scaled y value. 
            }

            @Override
            public void onAnalogMovedGetAngle(float angle) {
              //do something with the angle 
            }

            @Override
            public void onAnalogMovedGetQuadrant(Quadrant quadrant) {
              //do something with the quadrant. 
            }
        });
````
<h2>Todo</h2>
Add ability for haptic feedback.
Upload demo app to google play

<h2>Developed By</h2>
**Paul T**

<h2>License</h2>

Copyright 2014 Paul T

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


