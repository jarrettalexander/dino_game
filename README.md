dino_game
=========

What is it?
-----------

An Android application that let's you play a dinosaur game that utilizes
the player's real-world location to get items. Can easily be modified to
be a different theme/genre of game.

Installation using Eclipse
--------------------------

Clone the dino_game repo.
Open as New Android Project From Existing Code in Eclipse
Update the Android Maps API Key in the AndroidManifest.xml file to include a key for edu.uark.csce.mobile.dinogame.

Instructions for setting up an API key are [here](https://developers.google.com/maps/documentation/android/intro)

Under the DinoGame Properties -> Anrdroid tab, add appcompat_v7 and google-play-services-lib libraries if they are not already present.

Add the included AmbilWarna library project to your Eclipse workspace as an Android Project from Existing Code.
Under the DinoGame Properties -> Android tab, add the Ambil Warna library to the project.

Optionally:
-----------

Add the included LocationProvider to your workspace in order to provide mock location data to the app for testing.
Within the LocationProvider project, in the LocationUtils.java file, the WAYPOINTS_LAT, WAYPOINTS_LONG, and WAYPOINTS_ACCURACY can be edited to custom latitudes, longitudes, and accuracies, respectively, that the LocationProvider will broadcast once it is run.

To use the LocationProvider app, install it on the Android device with the DinoGame. Start the MockLocations app, and set the delay for enough time for you to switch applications. Set the send interval and click "Run once" to broadcast the locations specified in the LocationUtils.java. Exit the MockLocations app and launch the DinoGame app, clicking "Find Item" to begin seeing the mock location data.

Known Issues
------------

Background music persists after exiting application, however this does not affect app performance.

Item notifications may not take user to inventory screen on some devices.

App crashes when attempting to decode item image from byte array stored in local database. For now, a placeholder image packaged with the app is used.

Contributors
------------

[Jarrett Alexander](https://github.com/jarrettalexander)

[Colton Phillips](https://github.com/Coltron4)

[Richard Alvis](https://github.com/rga001)

Special thanks to [yukuku](https://code.google.com/p/android-color-picker/) for the color picker.
