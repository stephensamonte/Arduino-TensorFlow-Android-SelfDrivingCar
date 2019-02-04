# Arduino-TensorFlow-Android-SelfDrivingCar

This is a bit of a hectic project that I did in the past. 

Project can be found here: https://stephen-tan-website.firebaseapp.com/self-driving-rc.html 

The original plan was to control a remote control car via an onboard Arduino Microcontroller wchich recieved instructions via a computer connected via USB. The computer would use its camera and OpenCV Image Classification trained on detecting left, right, stright, reverse driving directions. 

I did not have success in using OpenCV's classifier. I got so frustrated that I abandoned OpenCV. I remember the OpenCV Documentation being very non-user firendly. So, I moved on to using TensorFlow which I had some familiarity with on Android. I started figuring out and testing how to send the microcontroller commands from and Android device which is why there is an Android Project on this repository. 

Side note: 
- I uploaded this project which I abandoned 2 years ago. I started this project December 2016 and abandoned it May 2017. I learned a lot from it.

# Journal
- 2017_03_19_23_16_17 [Showcase](https://www.youtube.com/watch?v=TwgRMI5cnf4)
- 2017 03 19 23 15 24 [Showcase](https://www.youtube.com/watch?v=LhU2R0o4xfg&feature=youtu.be)
- 2017_03_19_23_15_24 [Showcase](https://www.youtube.com/watch?v=RQpLlQxHpc0&feature=youtu.be)

# The Base of the Project was adapted from a Tensorflow Image Recognition Android Applicaiton: 
- Cloned from the Tensor Flow Demo Classification Andorid Project: https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android

# Link Referenced: 
http://nilhcem.com/android/custom-tensorflow-classifier
