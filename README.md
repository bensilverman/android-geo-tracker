# android-geo-tracker

An example of a set of Android services which logs device GPS geocoordinates at a set repeating interval, also logging battery life, and a device "heart beat" -- an indication that the device is actually powered on. Using the Android AlarmManager class, these services run as background processes as long as the containing application is open. 

The logged data -- userid (attached to a login authentication process, for instance), deviceid (the android device id), lon (device longitude), lat (device latitutde), created (timestamp), battery life (percentage) -- is written to a local database table, and then submitted at a set interval, via the LoopJ Android Asynchronous Http Client (http://loopj.com/android-async-http/), via HTTP post to a remote URL (where data can be logged to remote databases). 

The goal of this service set was to facilitate business logic around the following scenario:

1. Users are required to use an application for their work.
2. Their work involves driving, and driving specific routes (eg. law enforcement, shipping, transporation)
3. Their work requires they keep their device charged, and powered on.  
4. Their work requires they keep GPS enabled.
5. Their work occasionally requires driving in remote areas where GPS signals may exist, but data signals may not. 

By having a local data store of geopoints, battery activity, and device heartbeats, the remote data calls only clear local data when a remote Async Http response returns a successful response. By looking at the resulting datasets, a business can clearly evaluate driving routes, create maps based upon geopoints, and also audit a user's behavior around keeping their device on, their application open, and their battery charged. 

