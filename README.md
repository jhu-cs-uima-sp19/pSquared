# pSquared
UIMA Team N

Existing usernames & passwords:
<br>Username: talker@jhu.edu		Password: joanne
<br>Username: listener@jhu.edu		Password: joanne
<br>Username: counselor@jhu.edu 	Password: joanne	

General State of the App

We have completed all of our screens and aesthetic design, which are all fully functional.

Our main activity begins with the login / register screen. Register will allow the user to input a username and password that will be updated on our Firebase database. Once the user has created an account, they can log in (authentication done by Firebase).

Navigation has been completed. Screens such as the About page or Settings page are very standard Android pages. The About page is finished - its purpose is to inform users about the app. The Settings page uses SharedPreferences to update password, notification, and sign out.

The Home screens for both Talkers and Listeners feature large buttons. Once clicked, the buttons change accordingly to show that they will be connected as soon as an appropriate user is available.

In our database, our json has four components: a list of all users, a list of available talkers, a list of available listeners, and a helper component.

The json with the list of users is saved with the following key-value pairs= {key:username of user, value: 1 if (user is listener); 0 if (user is talker)}. The 0 or 1 value that determines if the user is a listener or talker can be changed from the counselor’s “Listeners” screen.

The list of available talkers and listeners are updated in real-time depending on whether the user has pushed the “Talk” or “Listen” buttons or not. The users disappear from the respective lists in real-time when they re-click “Talk” or “Listen”.

By using this database, we can authenticate availability of Talkers and Listeners and connect them into an anonymous chatbox. After exiting the chat, all messages are deleted from the database.

We have also implemented push notifications that alert talkers and listeners when a listener is currently available. Unfortunately, our notifications only work with APIs greater than or equal to 26 (this is something we will fix for our third sprint).

Stand-Up Meetings

March 28 - Meeting with Gaurav and Joanne

April 2 - Team meeting
Discussed how to allocate responsibilities
Made changes to UI design before submitting finalized design

April 3 - Team meeting
Finished all the xml files individually
Collectively connected all the screens to complete navigation

April 4 - Meeting with Gaurav
Configured all our git branches to the same repository
Configured Firebase to our app

April 8 - Team meeting
Looked up Chat SDK, Cloud Messaging, etc. and several other chat APIs
Used Firebase to instill a messaging feature into our app - several bugs present

April 11 - Meeting with Gaurav
Talked about how to implement messenger
Created individual databases do to 1-to-1 chats instead of large group chats

April 12 - Team meeting
Finished messaging feature
Ran into one bug wherein more than two chatters can enter a chat-box if the database has not updated quickly enough

April 14 - Team meeting
Fixed aforementioned bug by implementing a different database structure, but still ran into the same problem
Considered creating another Java listener to check if Firebase is empty

April 18 - Team meeting
Allocated responsibilities to the many bugs with the messaging feature of our app
Must press exit button twice to actually exit
If no message is sent yet, closing chat on one end will not kick other person out
General glitching of messages
Colour still don’t match - talkers should be pink; listeners blue

April 21 - Team meeting
First implementation of push notifications - did not work very well
Listeners get notified if they start to listen - this should not happen
Chatbox still glitches

April 25 - Team meeting with Gaurav
Chat crisis - chat message kept disappearing
Tried to figure out why app only works on certain emulators

April 29 - Team meeting
Prepared for in-class demo
Created presentation
Fixed bugs with chat (still many bugs with exiting!)
Got SharedPreferences to update settings

May 2 - Team meeting with Gaurav
Checked orientation
Kaitlyn’s and Jonghae’s Android Studio would not build
Fixed by deleting repo and re-cloning from master

May 3 - Team meeting
Push notifications do not work on target API!!!!
Fixed bug with update password

