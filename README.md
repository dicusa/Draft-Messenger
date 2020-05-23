# Draft-Messenger
 This is an one-to-one realtime chat application supported by firebase as backend.

## Application Overview
![Draft Messenger Application](images/ApplicationOverview/AppOverView.gif)

## Application Screens
[Application Screens](images/ApplicationScreens)

## Application Code
1. [Kotlin Code](app/src/main/java/com/example/chitchatkt)
2. [XML Code](app/src/main/res/layout)

## Application Description
It is a cloud based realtime one-to-one chat application. The chats are stored at the firebase in well structured manner.
Therefore it uses a small amount of user device memory.

1. Fronend :
   - It contains several features to create a chatting environment between two users.
   - It allows user to register with Email and Password.
   - It allows to click images from the application to send directly.
   - It provides sharing of images on two sizes(Selected by user):
     - Original Size (Link of Storage is generated)
     - Compressed Size (Actual image is displayed)

2. Backend:  
   - It uses Google Firebase as backend support.
   - To use Google Firebase : - https://firebase.google.com/docs/android/setup. You can visit and read the documentation to add Firebase to your application.
   - Application uses Realtime database to store chats and user data.
   - Application uses Authentication with Email and Password with Firebase Suppport.

## Advantages and Security?

1. It enables user to Signup and Signin with email and password. Which is handeld by Google Firebase Auth system itself.
2. It provides mobility of user chats over Firebase Realtime database.
3. User have the ability to choose between Image Quality
4. Chats and Images are secure at Firebase Database and Storage

### Dependencies used:
Check App's build.gradle file to check the dependencies:-
[Dependencies](app/build.gradle)